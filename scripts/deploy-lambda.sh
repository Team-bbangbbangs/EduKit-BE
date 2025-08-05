#!/bin/bash

set -e

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

# Layer 정의
declare -A LAYERS=(
    ["spring"]="Spring Framework core dependencies"
    ["database"]="Database and JPA dependencies"
    ["external"]="External service integrations"
    ["utils"]="Utilities (JSON, Logging)"
)

# Layer 배포 함수
deploy_layer() {
    local layer_type=$1
    local description=$2
    local layer_name="edukit-${layer_type}-layer-${ENVIRONMENT}"
    local zip_file="edukit-batch/build/distributions/layers/${layer_type}-layer.zip"

    if [[ ! -f "$zip_file" ]]; then
        return 1
    fi

    local zip_size=$(stat -f%z "$zip_file" 2>/dev/null || stat -c%s "$zip_file")
    if [[ $zip_size -eq 0 ]]; then
        return 1
    fi

    local layer_arn=$(aws lambda publish-layer-version \
        --layer-name "$layer_name" \
        --description "$description" \
        --zip-file "fileb://$zip_file" \
        --compatible-runtimes java21 java17 \
        --compatible-architectures x86_64 \
        --region $AWS_REGION \
        --query 'LayerVersionArn' \
        --output text 2>/dev/null)

    if [[ $? -eq 0 && -n "$layer_arn" ]]; then
        echo "$layer_arn"
        return 0
    else
        return 1
    fi
}

# Layer 배포
echo "📦 Layer 배포 중..."
declare -A LAYER_ARNS
deployed_count=0

for layer_type in "${!LAYERS[@]}"; do
    if layer_arn=$(deploy_layer "$layer_type" "${LAYERS[$layer_type]}"); then
        LAYER_ARNS[$layer_type]=$layer_arn
        ((deployed_count++))
    fi
done

if [[ $deployed_count -eq 0 ]]; then
    echo "⚠️ 배포된 Layer가 없습니다. Lambda 함수만 배포합니다."
    deployed_layers=()
    layer_args=""
else
    # 배포된 Layer ARN 목록 생성
    deployed_layers=()
    for layer_type in "${!LAYER_ARNS[@]}"; do
        deployed_layers+=("${LAYER_ARNS[$layer_type]}")
    done
    
    layer_args=$(IFS=' '; echo "${deployed_layers[*]}")
    echo "✅ ${deployed_count}개 Layer 배포 완료"
fi

# Lambda 함수 배포
echo "🔧 Lambda 함수 배포 중..."

function_zip="edukit-batch/build/distributions/lambda-function.zip"
if [[ ! -f "$function_zip" ]]; then
    echo "❌ Lambda 함수 ZIP 파일을 찾을 수 없습니다: $function_zip"
    exit 1
fi

# 함수 존재 여부 확인
if aws lambda get-function --function-name "$FUNCTION_NAME" --region $AWS_REGION &>/dev/null; then
    # 기존 함수 업데이트
    if [[ -n "$layer_args" ]]; then
        aws lambda update-function-configuration \
            --function-name "$FUNCTION_NAME" \
            --layers $layer_args \
            --memory-size $MEMORY_SIZE \
            --timeout $TIMEOUT \
            --region $AWS_REGION &>/dev/null
    else
        aws lambda update-function-configuration \
            --function-name "$FUNCTION_NAME" \
            --memory-size $MEMORY_SIZE \
            --timeout $TIMEOUT \
            --region $AWS_REGION &>/dev/null
    fi

    aws lambda wait function-updated \
        --function-name "$FUNCTION_NAME" \
        --region $AWS_REGION

    aws lambda update-function-code \
        --function-name "$FUNCTION_NAME" \
        --zip-file "fileb://$function_zip" \
        --region $AWS_REGION &>/dev/null
else
    # 새 함수 생성
    if [[ -n "$layer_args" ]]; then
        aws lambda create-function \
            --function-name "$FUNCTION_NAME" \
            --runtime java21 \
            --role "$LAMBDA_ROLE_ARN" \
            --handler "com.edukit.batch.handler.TeacherVerificationLambdaHandler::handleRequest" \
            --zip-file "fileb://$function_zip" \
            --layers $layer_args \
            --timeout $TIMEOUT \
            --memory-size $MEMORY_SIZE \
            --environment Variables="{SPRING_PROFILES_ACTIVE=$ENVIRONMENT}" \
            --region $AWS_REGION &>/dev/null
    else
        aws lambda create-function \
            --function-name "$FUNCTION_NAME" \
            --runtime java21 \
            --role "$LAMBDA_ROLE_ARN" \
            --handler "com.edukit.batch.handler.TeacherVerificationLambdaHandler::handleRequest" \
            --zip-file "fileb://$function_zip" \
            --timeout $TIMEOUT \
            --memory-size $MEMORY_SIZE \
            --environment Variables="{SPRING_PROFILES_ACTIVE=$ENVIRONMENT}" \
            --region $AWS_REGION &>/dev/null
    fi
fi

# 함수 업데이트 완료 대기
aws lambda wait function-updated \
    --function-name "$FUNCTION_NAME" \
    --region $AWS_REGION

echo "✅ Lambda 함수 배포 완료"

# 배치 실행 (옵션)
if [[ "${EXECUTE_BATCH:-false}" == "true" ]]; then
    echo "🚀 배치 실행 중..."

    if aws lambda invoke \
        --function-name "$FUNCTION_NAME" \
        --payload '{}' \
        --cli-binary-format raw-in-base64-out \
        --region $AWS_REGION \
        response.json &>/dev/null; then
        
        if [[ -f "response.json" ]]; then
            if grep -q '"errorMessage"' response.json 2>/dev/null; then
                echo "⚠️ 배치 실행 실패 (배포는 성공)"
                echo "Response:"
                cat response.json
                rm -f response.json
            else
                echo "✅ 배치 실행 완료"
                if [[ -s "response.json" ]]; then
                    echo "Response:"
                    cat response.json
                fi
                rm -f response.json
            fi
        fi
    else
        echo "⚠️ 배치 실행 실패 (Lambda 호출 오류, 배포는 성공)"
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
    echo "📄 Layer ARN 정보 저장 중..."
    
    # JSON 파일 생성 (더 안전한 방법)
    json_file="layer-arns-${ENVIRONMENT}.json"
    
    # 기본 JSON 구조 생성
    cat > "$json_file" << EOF
{
  "environment": "$ENVIRONMENT",
  "region": "$AWS_REGION", 
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "function_name": "$FUNCTION_NAME",
  "layers": {
EOF
    
    # Layer ARN 정보 추가
    if [[ ${#LAYER_ARNS[@]} -gt 0 ]]; then
        layer_entries=()
        for layer_type in "${!LAYER_ARNS[@]}"; do
            layer_entries+=("    \"${layer_type}\": \"${LAYER_ARNS[$layer_type]}\"")
        done
        
        # 마지막 항목을 제외하고 쉼표 추가
        for ((i=0; i<${#layer_entries[@]}-1; i++)); do
            echo "${layer_entries[i]}," >> "$json_file"
        done
        
        # 마지막 항목은 쉼표 없이 추가
        if [[ ${#layer_entries[@]} -gt 0 ]]; then
            echo "${layer_entries[-1]}" >> "$json_file"
        fi
    fi
    
    # JSON 닫기
    cat >> "$json_file" << EOF
  }
}
EOF
    
    if [[ -f "$json_file" ]]; then
        echo "✅ Layer ARN 정보가 $json_file에 저장되었습니다."
        
        # 파일 내용 검증 (jq가 있다면)
        if command -v jq &> /dev/null && jq empty "$json_file" 2>/dev/null; then
            echo "📋 JSON 파일 검증 완료"
        else
            echo "📄 생성된 JSON 파일:"
            cat "$json_file"
        fi
    else
        echo "❌ Layer ARN 파일 생성에 실패했습니다"
    fi
fi
