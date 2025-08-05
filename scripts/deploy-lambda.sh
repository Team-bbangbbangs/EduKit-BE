#!/bin/bash

set -e
set -o pipefail

# 디버깅 모드 (선택적)
if [[ "${DEBUG:-false}" == "true" ]]; then
    set -x
fi

# 예상치 못한 종료 감지
cleanup_on_exit() {
    local exit_code=$?
    if [[ $exit_code -ne 0 ]]; then
        echo "" >&2
        echo "🚨 스크립트가 예상치 못하게 종료되었습니다 (exit code: $exit_code)" >&2
        
        # Exit code 해석
        case $exit_code in
            126) 
                echo "❗ Exit code 126: 실행 권한 문제 또는 명령어 실행 불가" >&2
                echo "💡 해결 방법:" >&2
                echo "   - 스크립트 실행 권한: chmod +x $0" >&2
                echo "   - AWS CLI 실행 권한 확인" >&2
                echo "   - 필요한 바이너리가 PATH에 있는지 확인" >&2
                ;;
            127)
                echo "❗ Exit code 127: 명령어를 찾을 수 없음" >&2
                echo "💡 AWS CLI가 설치되어 있고 PATH에 있는지 확인해주세요" >&2
                ;;
            1)
                echo "❗ Exit code 1: 일반적인 에러" >&2
                echo "💡 위의 에러 메시지를 참고해주세요" >&2
                ;;
        esac
        
        echo "⏰ 종료 시간: $(date '+%Y-%m-%d %H:%M:%S')" >&2
        echo "📍 문제 해결 후 다시 시도해주세요" >&2
        echo "" >&2
    fi
}

trap cleanup_on_exit EXIT

# 스크립트 실행 권한 자가 진단
echo "🔍 스크립트 실행 환경 진단..."
script_path="$0"
script_permissions=$(ls -la "$script_path" | awk '{print $1}')
echo "📋 스크립트 권한: $script_path ($script_permissions)"

if [[ ! -x "$script_path" ]]; then
    echo "⚠️  스크립트에 실행 권한이 없습니다. 자동으로 권한을 부여합니다."
    chmod +x "$script_path" || {
        echo "❌ 실행 권한 부여 실패"
        exit 1
    }
    echo "✅ 실행 권한 부여 완료"
fi

# 스크립트 디렉토리 확인 (프로젝트 루트에서 실행되었는지 체크)
if [[ ! -f "gradlew" || ! -f "settings.gradle" ]]; then
    echo "❌ 프로젝트 루트 디렉토리에서 실행해주세요."
    echo "현재 위치: $(pwd)"
    echo "사용법: ./scripts/deploy-lambda.sh <environment>"
    exit 1
fi

# 환경 인수 확인
ENVIRONMENT="${1:-dev}"
if [[ ! "$ENVIRONMENT" =~ ^(dev|prod)$ ]]; then
    echo "❌ 지원하지 않는 환경입니다: $ENVIRONMENT"
    echo "사용법: ./scripts/deploy-lambda.sh <dev|prod>"
    exit 1
fi

# 환경별 설정
case "$ENVIRONMENT" in
    "dev")
        AWS_REGION="${AWS_REGION:-ap-northeast-2}"
        FUNCTION_NAME="edukit-batch-teacher-verification-dev"
        MEMORY_SIZE=1024
        TIMEOUT=900
        ;;
    "prod")
        AWS_REGION="${AWS_REGION:-ap-northeast-2}"
        FUNCTION_NAME="edukit-batch-teacher-verification-prod"
        MEMORY_SIZE=1536  # 프로덕션은 더 많은 메모리
        TIMEOUT=900
        ;;
esac

# 필수 환경 변수 확인
if [[ -z "$LAMBDA_ROLE_ARN" ]]; then
    echo "❌ LAMBDA_ROLE_ARN 환경 변수가 설정되지 않았습니다."
    echo "예시: export LAMBDA_ROLE_ARN=arn:aws:iam::123456789012:role/lambda-execution-role"
    exit 1
fi

# AWS CLI 설치 확인
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI가 설치되지 않았습니다."
    exit 1
fi

# AWS 자격증명 확인
if ! aws sts get-caller-identity &> /dev/null; then
    echo "❌ AWS 자격증명이 설정되지 않았습니다."
    echo "aws configure를 실행하거나 환경 변수를 설정해주세요."
    exit 1
fi

echo "🚀 EduKit Batch Lambda 배포 시작"
echo "Environment: $ENVIRONMENT"
echo "Region: $AWS_REGION"
echo "Function: $FUNCTION_NAME"

# Gradle 빌드
echo "🔨 Gradle 빌드 실행 중..."
./gradlew :edukit-batch:buildLambda --no-daemon --quiet

# Layer 정의 (의존성 순서대로 배열 사용: Base -> Core -> External)
LAYER_TYPES=("base" "core" "external")

# Bash 3.x 호환성을 위한 대체 함수  
get_layer_description() {
    case "$1" in
        "base") echo "Base dependencies (Common utilities, Jackson, Logback)" ;;
        "core") echo "Core dependencies (Spring Framework, JPA/Hibernate)" ;;
        "external") echo "External dependencies (AWS SDK, MySQL driver)" ;;
        *) echo "Unknown layer: $1" ;;
    esac
}

# Layer 배포 함수 (재시도 로직 포함)
deploy_layer() {
    local layer_type=$1
    local description=$2
    local layer_name="edukit-${layer_type}-layer-${ENVIRONMENT}"
    local zip_file="edukit-batch/build/distributions/layers/${layer_type}-layer.zip"
    local max_retries=3
    local retry_delay=10

    if [[ ! -f "$zip_file" ]]; then
        echo "❌ Layer 파일 없음: $zip_file" >&2
        return 1
    fi

    local zip_size=$(stat -f%z "$zip_file" 2>/dev/null || stat -c%s "$zip_file")
    if [[ $zip_size -eq 0 ]]; then
        echo "❌ Layer 파일이 비어있음: $zip_file" >&2
        return 1
    fi

    # Layer 크기 확인 (250MB = 262,144,000 bytes)
    if [[ $zip_size -gt 262144000 ]]; then
        echo "❌ Layer 크기 초과: $(($zip_size / 1024 / 1024))MB > 250MB" >&2
        return 1
    fi

    echo "📤 Layer 배포 시도: $layer_name ($(($zip_size / 1024 / 1024))MB)" >&2
    
    for attempt in $(seq 1 $max_retries); do
        echo "  🔄 시도 $attempt/$max_retries..." >&2
        
        local layer_arn=$(aws lambda publish-layer-version \
            --layer-name "$layer_name" \
            --description "$description" \
            --zip-file "fileb://$zip_file" \
            --compatible-runtimes java21 \
            --compatible-architectures x86_64 \
            --region $AWS_REGION \
            --query 'LayerVersionArn' \
            --output text 2>&1)


        local exit_code=$?
        
        if [[ $exit_code -eq 0 && -n "$layer_arn" && ! "$layer_arn" =~ "error" ]]; then
            echo "  ✅ Layer 배포 성공!" >&2
            echo "$layer_arn"  # ARN만 stdout으로 출력
            return 0
        else
            echo "  ⚠️  시도 $attempt 실패: $layer_arn" >&2
            if [[ $attempt -lt $max_retries ]]; then
                echo "  ⏳ ${retry_delay}초 대기 후 재시도..." >&2
                sleep $retry_delay
                retry_delay=$((retry_delay + 5)) # 점진적 지연 증가
            fi
        fi
    done

    echo "  ❌ 모든 재시도 실패" >&2
    return 1
}

# Layer 배포
echo "📦 Layer 배포 중..."
deployed_count=0

# Layer ARN 저장을 위한 변수들 초기화 (의존성 순서대로)
BASE_LAYER_ARN=""
CORE_LAYER_ARN=""
EXTERNAL_LAYER_ARN=""

# Layer 파일들 존재 여부 및 크기 확인
echo "🔍 Layer 파일 상태 확인:"
total_layer_size=0
missing_layers=()

for layer_type in "${LAYER_TYPES[@]}"; do
    zip_file="edukit-batch/build/distributions/layers/${layer_type}-layer.zip"
    if [[ -f "$zip_file" ]]; then
        file_size=$(stat -f%z "$zip_file" 2>/dev/null || stat -c%s "$zip_file")
        file_size_mb=$((file_size / 1024 / 1024))
        total_layer_size=$((total_layer_size + file_size_mb))
        
        # 개별 Layer 크기 경고 (50MB 초과)
        if [[ $file_size_mb -gt 50 ]]; then
            echo "  ⚠️  ${layer_type}-layer.zip: ${file_size} bytes (${file_size_mb}MB - 큰 용량!)"
        else
            echo "  ✅ ${layer_type}-layer.zip: ${file_size} bytes (${file_size_mb}MB)"
        fi
    else
        echo "  ❌ ${layer_type}-layer.zip: 파일 없음"
        missing_layers+=("$layer_type")
    fi
done

# 전체 Layer 크기 경고
if [[ $total_layer_size -gt 250 ]]; then
    echo "⚠️  전체 Layer 크기가 큽니다: ${total_layer_size}MB (최적화 권장)"
else
    echo "✅ 전체 Layer 크기: ${total_layer_size}MB"
fi

# 누락된 Layer가 있으면 경고
if [[ ${#missing_layers[@]} -gt 0 ]]; then
    echo "⚠️  누락된 Layer: ${missing_layers[*]}"
fi

# ================================
# 📋 배포 전 전체 검증 단계
# ================================
echo ""
echo "🔍 배포 전 전체 검증 시작..."

# 1. 필수 환경 변수 확인
echo "📋 1. 환경 변수 검증:"
validation_failed=false

if [[ -z "$AWS_REGION" ]]; then
    echo "  ❌ AWS_REGION이 설정되지 않았습니다"
    validation_failed=true
else
    echo "  ✅ AWS_REGION: $AWS_REGION"
fi

if [[ -z "$ENVIRONMENT" ]]; then
    echo "  ❌ ENVIRONMENT가 설정되지 않았습니다"
    validation_failed=true
else
    echo "  ✅ ENVIRONMENT: $ENVIRONMENT"
fi

# 2. Layer ZIP 파일 전체 검증
echo "📋 2. Layer ZIP 파일 전체 검증:"
valid_layers=()
invalid_layers=()

for layer_type in "${LAYER_TYPES[@]}"; do
    zip_file="edukit-batch/build/distributions/layers/${layer_type}-layer.zip"
    layer_name="edukit-${layer_type}-layer-${ENVIRONMENT}"
    
    # ZIP 파일 존재 여부
    if [[ ! -f "$zip_file" ]]; then
        echo "  ❌ ${layer_type}: ZIP 파일 없음 ($zip_file)"
        invalid_layers+=("$layer_type")
        continue
    fi
    
    # ZIP 파일 크기 확인
    file_size=$(stat -f%z "$zip_file" 2>/dev/null || stat -c%s "$zip_file")
    if [[ $file_size -eq 0 ]]; then
        echo "  ❌ ${layer_type}: ZIP 파일이 비어있음 ($zip_file)"
        invalid_layers+=("$layer_type")
        continue
    fi
    
    # Layer 이름 검증
    if [[ -z "$layer_name" ]]; then
        echo "  ❌ ${layer_type}: Layer 이름이 비어있음"
        invalid_layers+=("$layer_type")
        continue
    fi
    
    # Layer 설명 검증
    description=$(get_layer_description "$layer_type")
    if [[ -z "$description" ]]; then
        echo "  ❌ ${layer_type}: Layer 설명이 비어있음"
        invalid_layers+=("$layer_type")
        continue
    fi
    
    file_size_mb=$((file_size / 1024 / 1024))
    echo "  ✅ ${layer_type}: ${file_size_mb}MB, Layer명: $layer_name"
    valid_layers+=("$layer_type")
done

# 3. AWS CLI 접근 권한 확인
echo "📋 3. AWS CLI 접근 권한 확인:"
if ! aws sts get-caller-identity --region "$AWS_REGION" > /dev/null 2>&1; then
    echo "  ❌ AWS CLI 인증 실패 - AWS 권한을 확인해주세요"
    validation_failed=true
else
    echo "  ✅ AWS CLI 인증 성공"
fi

# 4. 검증 결과 요약
echo ""
echo "📊 검증 결과 요약:"
echo "  ✅ 유효한 Layer: ${#valid_layers[@]}개 (${valid_layers[*]})"
if [[ ${#invalid_layers[@]} -gt 0 ]]; then
    echo "  ❌ 무효한 Layer: ${#invalid_layers[@]}개 (${invalid_layers[*]})"
fi

# 5. 검증 실패 시 스크립트 중단
if [[ "$validation_failed" == "true" || ${#valid_layers[@]} -eq 0 ]]; then
    echo ""
    echo "🚨 검증 실패! 배포를 중단합니다."
    echo "💡 위의 문제들을 해결한 후 다시 시도해주세요."
    exit 1
fi

echo ""
echo "✅ 모든 검증 통과! 배포를 시작합니다."
echo "🔄 총 ${#valid_layers[@]}개 Layer 순차 배포 시작..."

# 검증된 Layer만 배포 대상으로 설정
VALIDATED_LAYERS=("${valid_layers[@]}")

for i in "${!VALIDATED_LAYERS[@]}"; do
    layer_type="${VALIDATED_LAYERS[$i]}"
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📋 Layer $((i+1))/${#VALIDATED_LAYERS[@]}: $layer_type"
    echo "⏰ 시작 시간: $(date '+%Y-%m-%d %H:%M:%S')"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    # 검증된 Layer이므로 파일 존재 확인 불필요 (이미 검증됨)
    zip_file="edukit-batch/build/distributions/layers/${layer_type}-layer.zip"

    echo "🚀 ${layer_type} layer 배포 시도 중..."
    description=$(get_layer_description "$layer_type")
    if layer_arn=$(deploy_layer "$layer_type" "$description"); then
        echo "  ✅ ${layer_type} layer 배포 성공: $layer_arn"
        echo "  📝 Layer ARN을 변수에 저장 중..."
        
        # Bash 3.x 호환성을 위해 associative array 대신 변수 사용
        case "$layer_type" in
            "base") 
                BASE_LAYER_ARN="$layer_arn"
                echo "  💾 BASE_LAYER_ARN 저장 완료"
                ;;
            "core") 
                CORE_LAYER_ARN="$layer_arn"
                echo "  💾 CORE_LAYER_ARN 저장 완료"
                ;;
            "external") 
                EXTERNAL_LAYER_ARN="$layer_arn"
                echo "  💾 EXTERNAL_LAYER_ARN 저장 완료"
                ;;
        esac
        
        # 카운터 증가 (안전하게)
        deployed_count=$((deployed_count + 1))
        echo "  📊 배포 완료된 Layer 수: $deployed_count/${#VALIDATED_LAYERS[@]}"
        
        # 다음 Layer 배포 전 대기 (마지막 Layer 제외, AWS API rate limiting 회피)
        if [[ $i -lt $((${#VALIDATED_LAYERS[@]} - 1)) ]]; then
            next_index=$((i + 1))
            next_layer="${VALIDATED_LAYERS[$next_index]}"
            echo "  ⏳ AWS API 제한 회피를 위해 10초 대기..."
            echo "  💭 대기 시작: $(date '+%H:%M:%S')"
            echo "  🔮 다음 배포 예정: $next_layer ($((next_index + 1))/${#VALIDATED_LAYERS[@]})"
            
            sleep 10 || {
                echo "  ⚠️ sleep 명령 실패 - 계속 진행" >&2
            }
            
            echo "  💭 대기 완료: $(date '+%H:%M:%S')"
            echo "  🔄 $next_layer Layer 배포 준비 완료!"
        else
            echo "  🎯 마지막 Layer 배포 완료 - 대기 없음"
        fi
    else
        echo "  ❌ ${layer_type} layer 배포 실패 (계속 진행)"
        # 실패해도 다음 Layer를 위해 짧은 대기
        if [[ $i -lt $((${#VALIDATED_LAYERS[@]} - 1)) ]]; then
            echo "  ⏳ 3초 대기 후 다음 Layer 시도..."
            sleep 3 || {
                echo "  ⚠️ sleep 명령 실패 - 계속 진행" >&2
            }
        fi
    fi
    
    echo "✅ Layer $((i+1))/${#VALIDATED_LAYERS[@]} ($layer_type) 처리 완료"
    echo "⏰ 완료 시간: $(date '+%Y-%m-%d %H:%M:%S')"
done

echo ""
echo "🏁 모든 Layer 배포 시도 완료!"
echo "📊 최종 결과: $deployed_count/${#VALIDATED_LAYERS[@]} Layer 배포 성공"
echo "⏰ 전체 완료 시간: $(date '+%Y-%m-%d %H:%M:%S')"

if [[ $deployed_count -eq 0 ]]; then
    echo "⚠️ 배포된 Layer가 없습니다. Layer 없이 함수만 배포합니다."
    echo "💡 의존성이 모두 exclude 되었거나 layer 분류에 문제가 있을 수 있습니다."
    # Layer 없이 배포하기 위해 빈 배열 설정
    deployed_layers=()
    layer_args=""
else
    echo "✅ ${deployed_count}개 Layer 배포 완료"
    
    # 배포된 Layer ARN 목록 생성 (의존성 순서대로: Base -> Core -> External)
    deployed_layers=()
    [[ -n "$BASE_LAYER_ARN" ]] && deployed_layers+=("$BASE_LAYER_ARN")
    [[ -n "$CORE_LAYER_ARN" ]] && deployed_layers+=("$CORE_LAYER_ARN")
    [[ -n "$EXTERNAL_LAYER_ARN" ]] && deployed_layers+=("$EXTERNAL_LAYER_ARN")
    
    layer_args=$(IFS=' '; echo "${deployed_layers[*]}")
fi

# ================================
# 🔧 Lambda 함수 배포
# ================================
echo ""
echo "🔧 Lambda 함수 배포 중..."

# 1. Lambda 함수 ZIP 파일 검증
echo "📋 Lambda 함수 ZIP 파일 검증:"
function_zip="edukit-batch/build/distributions/lambda-function.zip"

if [[ ! -f "$function_zip" ]]; then
    echo "  ❌ Lambda 함수 ZIP 파일을 찾을 수 없습니다: $function_zip"
    echo "  💡 Gradle 빌드가 완료되었는지 확인해주세요:"
    echo "     ./gradlew :edukit-batch:buildFunctionZip"
    exit 1
fi

# ZIP 파일 크기 및 권한 확인
file_size=$(stat -f%z "$function_zip" 2>/dev/null || stat -c%s "$function_zip")
file_size_mb=$((file_size / 1024 / 1024))
file_permissions=$(ls -la "$function_zip" | awk '{print $1}')

if [[ $file_size -eq 0 ]]; then
    echo "  ❌ Lambda 함수 ZIP 파일이 비어있습니다: $function_zip"
    exit 1
fi

echo "  ✅ Lambda 함수 ZIP: ${file_size_mb}MB ($file_permissions)"

# 2. 필수 변수 재검증
echo "📋 Lambda 함수 배포 변수 검증:"
echo "  ✅ FUNCTION_NAME: $FUNCTION_NAME"
echo "  ✅ LAMBDA_ROLE_ARN: $LAMBDA_ROLE_ARN"
echo "  ✅ MEMORY_SIZE: ${MEMORY_SIZE}MB"
echo "  ✅ TIMEOUT: ${TIMEOUT}초"

# 3. AWS CLI 실행 권한 재확인
echo "📋 AWS CLI 명령어 실행 가능성 검증:"
aws_cli_path=$(which aws)
if [[ -z "$aws_cli_path" ]]; then
    echo "  ❌ AWS CLI를 찾을 수 없습니다"
    exit 1
fi

aws_permissions=$(ls -la "$aws_cli_path" | awk '{print $1}')
echo "  ✅ AWS CLI: $aws_cli_path ($aws_permissions)"

echo ""
echo "🚀 Lambda 함수 배포 시작..."

# 함수 존재 여부 확인
echo "🔍 기존 Lambda 함수 확인 중..."
if aws lambda get-function --function-name "$FUNCTION_NAME" --region $AWS_REGION >/dev/null 2>&1; then
    echo "  📍 기존 함수 발견 - 업데이트 모드"
    
    # 기존 함수 구성 업데이트
    echo "  🔧 함수 구성 업데이트 중..."
    if [[ -n "$layer_args" ]]; then
        echo "    📦 Layer 적용: $layer_args"
        if ! aws lambda update-function-configuration \
            --function-name "$FUNCTION_NAME" \
            --layers $layer_args \
            --memory-size $MEMORY_SIZE \
            --timeout $TIMEOUT \
            --region $AWS_REGION >/dev/null 2>&1; then
            echo "  ❌ 함수 구성 업데이트 실패 (Layer 포함)"
            echo "  💡 Layer ARN이 유효한지 확인해주세요"
            exit 1
        fi
    else
        echo "    📦 Layer 없이 구성 업데이트"
        if ! aws lambda update-function-configuration \
            --function-name "$FUNCTION_NAME" \
            --memory-size $MEMORY_SIZE \
            --timeout $TIMEOUT \
            --region $AWS_REGION >/dev/null 2>&1; then
            echo "  ❌ 함수 구성 업데이트 실패"
            exit 1
        fi
    fi
    echo "  ✅ 함수 구성 업데이트 완료"

    echo "  ⏳ 함수 구성 업데이트 완료 대기 중..."
    if ! aws lambda wait function-updated \
        --function-name "$FUNCTION_NAME" \
        --region $AWS_REGION; then
        echo "  ❌ 함수 구성 업데이트 대기 실패"
        exit 1
    fi
    echo "  ✅ 함수 구성 업데이트 완료 확인"

    echo "  📂 함수 코드 업데이트 중..."
    if ! aws lambda update-function-code \
        --function-name "$FUNCTION_NAME" \
        --zip-file "fileb://$function_zip" \
        --region $AWS_REGION >/dev/null 2>&1; then
        echo "  ❌ 함수 코드 업데이트 실패"
        echo "  💡 ZIP 파일이 손상되었거나 너무 클 수 있습니다"
        exit 1
    fi
    echo "  ✅ 함수 코드 업데이트 완료"
else
    echo "  🆕 새 함수 생성 모드"
    
    # 새 함수 생성
    echo "  🔧 새 Lambda 함수 생성 중..."
    if [[ -n "$layer_args" ]]; then
        echo "    📦 Layer 포함: $layer_args"
        if ! aws lambda create-function \
            --function-name "$FUNCTION_NAME" \
            --runtime java21 \
            --role "$LAMBDA_ROLE_ARN" \
            --handler "com.edukit.batch.handler.TeacherVerificationLambdaHandler::handleRequest" \
            --zip-file "fileb://$function_zip" \
            --layers $layer_args \
            --timeout $TIMEOUT \
            --memory-size $MEMORY_SIZE \
            --environment Variables="{SPRING_PROFILES_ACTIVE=$ENVIRONMENT}" \
            --region $AWS_REGION >/dev/null 2>&1; then
            echo "  ❌ 새 함수 생성 실패 (Layer 포함)"
            echo "  💡 IAM 역할이나 Layer ARN을 확인해주세요"
            exit 1
        fi
    else
        echo "    📦 Layer 없이 생성"
        if ! aws lambda create-function \
            --function-name "$FUNCTION_NAME" \
            --runtime java21 \
            --role "$LAMBDA_ROLE_ARN" \
            --handler "com.edukit.batch.handler.TeacherVerificationLambdaHandler::handleRequest" \
            --zip-file "fileb://$function_zip" \
            --timeout $TIMEOUT \
            --memory-size $MEMORY_SIZE \
            --environment Variables="{SPRING_PROFILES_ACTIVE=$ENVIRONMENT}" \
            --region $AWS_REGION >/dev/null 2>&1; then
            echo "  ❌ 새 함수 생성 실패"
            echo "  💡 IAM 역할이나 ZIP 파일을 확인해주세요"
            exit 1
        fi
    fi
    echo "  ✅ 새 함수 생성 완료"
fi

# 함수 업데이트 완료 대기
echo "⏳ 최종 함수 상태 확인 중..."
if ! aws lambda wait function-updated \
    --function-name "$FUNCTION_NAME" \
    --region $AWS_REGION; then
    echo "❌ 함수 상태 확인 실패"
    echo "💡 함수가 부분적으로 생성되었을 수 있습니다. AWS 콘솔에서 확인해주세요"
    exit 1
fi

echo ""
echo "✅ Lambda 함수 배포 완료!"
echo "📋 배포된 함수 정보:"
echo "  🎯 함수명: $FUNCTION_NAME"
echo "  🏷️  런타임: java21"
echo "  💾 메모리: ${MEMORY_SIZE}MB"
echo "  ⏱️  타임아웃: ${TIMEOUT}초"
if [[ -n "$layer_args" ]]; then
    echo "  📦 적용된 Layer: $(echo $layer_args | wc -w)개"
fi

# CloudWatch 로그 그룹 확인
echo ""
echo "📊 CloudWatch 로그 설정 확인..."
LOG_GROUP_NAME="/aws/lambda/$FUNCTION_NAME"

if aws logs describe-log-groups --log-group-name-prefix "$LOG_GROUP_NAME" --region $AWS_REGION --query 'logGroups[0].logGroupName' --output text 2>/dev/null | grep -q "$LOG_GROUP_NAME"; then
    echo "  ✅ CloudWatch 로그 그룹 존재: $LOG_GROUP_NAME"
else
    echo "  ⚠️  CloudWatch 로그 그룹이 아직 생성되지 않았습니다"
    echo "  💡 첫 번째 Lambda 실행 시 자동으로 생성됩니다"
fi

# 배치 실행 (옵션)
if [[ "${EXECUTE_BATCH:-false}" == "true" ]]; then
    echo ""
    echo "🚀 배치 테스트 실행 중..."

    # Lambda 함수 비동기 호출
    INVOKE_TIME=$(date -u +%Y-%m-%dT%H:%M:%S)
    aws lambda invoke \
        --function-name "$FUNCTION_NAME" \
        --payload '{}' \
        --cli-binary-format raw-in-base64-out \
        --region $AWS_REGION \
        response.json &>/dev/null

    if [[ -f "response.json" ]]; then
        echo "📄 Lambda 응답:"
        cat response.json
        echo ""
        
        if grep -q '"errorMessage"' response.json 2>/dev/null; then
            echo "❌ 배치 실행 실패"
            
            # CloudWatch 로그 확인 시도
            echo "🔍 CloudWatch 로그 확인 중..."
            sleep 5  # 로그가 CloudWatch에 전송될 시간 대기
            
            # 최근 로그 이벤트 확인
            if aws logs describe-log-streams --log-group-name "$LOG_GROUP_NAME" --region $AWS_REGION --query 'logStreams[0].logStreamName' --output text 2>/dev/null | grep -q "."; then
                LATEST_STREAM=$(aws logs describe-log-streams --log-group-name "$LOG_GROUP_NAME" --region $AWS_REGION --order-by LastEventTime --descending --limit 1 --query 'logStreams[0].logStreamName' --output text)
                echo "  📋 최신 로그 스트림: $LATEST_STREAM"
                
                echo "  📝 최근 로그 이벤트 (최대 10개):"
                aws logs get-log-events \
                    --log-group-name "$LOG_GROUP_NAME" \
                    --log-stream-name "$LATEST_STREAM" \
                    --region $AWS_REGION \
                    --limit 10 \
                    --query 'events[*].[timestamp,message]' \
                    --output table 2>/dev/null || echo "  ⚠️  로그 이벤트를 가져올 수 없습니다"
            else
                echo "  ⚠️  로그 스트림을 찾을 수 없습니다"
            fi
            
            rm -f response.json
            exit 1
        else
            echo "✅ 배치 실행 완료"
            
            # 성공한 경우에도 로그 확인
            echo "🔍 실행 로그 확인 중..."
            sleep 3
            
            if aws logs describe-log-streams --log-group-name "$LOG_GROUP_NAME" --region $AWS_REGION --query 'logStreams[0].logStreamName' --output text 2>/dev/null | grep -q "."; then
                LATEST_STREAM=$(aws logs describe-log-streams --log-group-name "$LOG_GROUP_NAME" --region $AWS_REGION --order-by LastEventTime --descending --limit 1 --query 'logStreams[0].logStreamName' --output text)
                echo "  📋 로그 확인: https://console.aws.amazon.com/cloudwatch/home?region=$AWS_REGION#logsV2:log-groups/log-group/$(echo "$LOG_GROUP_NAME" | sed 's/\//%252F/g')/log-events/$(echo "$LATEST_STREAM" | sed 's/\//%252F/g')"
            fi
            
            rm -f response.json
        fi
    else
        echo "❌ Lambda 응답 파일을 찾을 수 없습니다"
        exit 1
    fi
fi

# 정리
./gradlew :edukit-batch:cleanLambda --quiet 2>/dev/null || true

echo "🎉 배포 완료!"
echo "Function: $FUNCTION_NAME"
echo "Layers: ${deployed_count}개"
echo "Region: $AWS_REGION"

# Layer ARN 정보를 파일로 저장 (선택사항)
if [[ "${SAVE_LAYER_ARNS:-false}" == "true" ]]; then
    # JSON 생성을 위한 배열 구성 (의존성 순서대로)
    layer_json_parts=()
    [[ -n "$BASE_LAYER_ARN" ]] && layer_json_parts+=("    \"base-layer\": \"$BASE_LAYER_ARN\"")
    [[ -n "$CORE_LAYER_ARN" ]] && layer_json_parts+=("    \"core-layer\": \"$CORE_LAYER_ARN\"")
    [[ -n "$EXTERNAL_LAYER_ARN" ]] && layer_json_parts+=("    \"external-layer\": \"$EXTERNAL_LAYER_ARN\"")
    
    # 마지막 요소를 제외하고 쉼표 추가
    layer_json=""
    for i in "${!layer_json_parts[@]}"; do
        layer_json+="${layer_json_parts[$i]}"
        if [[ $i -lt $((${#layer_json_parts[@]} - 1)) ]]; then
            layer_json+=","
        fi
        layer_json+=$'\n'
    done
    
    cat > "layer-arns-${ENVIRONMENT}.json" << EOF
{
  "environment": "$ENVIRONMENT",
  "region": "$AWS_REGION",
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "layers": {
${layer_json}  }
}
EOF
    echo "📄 Layer ARN 정보가 layer-arns-${ENVIRONMENT}.json에 저장되었습니다."
fi
