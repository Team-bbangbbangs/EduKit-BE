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
    ["common-core"]="Common-Core (Spring Framework) dependencies"
    ["database-orm"]="Database/ORM (Hibernate, JPA) dependencies"  
    ["external-services"]="External service integrations (AWS, etc)"
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

# Layer 파일들 존재 여부 먼저 확인
echo "🔍 Layer 파일 상태 확인:"
for layer_type in "${!LAYERS[@]}"; do
    zip_file="edukit-batch/build/distributions/layers/${layer_type}-layer.zip"
    if [[ -f "$zip_file" ]]; then
        file_size=$(stat -f%z "$zip_file" 2>/dev/null || stat -c%s "$zip_file")
        echo "  ✅ ${layer_type}-layer.zip: ${file_size} bytes"
    else
        echo "  ❌ ${layer_type}-layer.zip: 파일 없음"
    fi
done

for layer_type in "${!LAYERS[@]}"; do
    echo "🚀 ${layer_type} layer 배포 시도 중..."
    if layer_arn=$(deploy_layer "$layer_type" "${LAYERS[$layer_type]}"); then
        echo "  ✅ ${layer_type} layer 배포 성공: $layer_arn"
        LAYER_ARNS[$layer_type]=$layer_arn
        ((deployed_count++))
    else
        echo "  ❌ ${layer_type} layer 배포 실패"
    fi
done

if [[ $deployed_count -eq 0 ]]; then
    echo "⚠️ 배포된 Layer가 없습니다. Layer 없이 함수만 배포합니다."
    echo "💡 의존성이 모두 exclude 되었거나 layer 분류에 문제가 있을 수 있습니다."
    # Layer 없이 배포하기 위해 빈 배열 설정
    deployed_layers=()
    layer_args=""
else
    echo "✅ ${deployed_count}개 Layer 배포 완료"
    
    # 배포된 Layer ARN 목록 생성
    deployed_layers=()
    for layer_type in "${!LAYER_ARNS[@]}"; do
        deployed_layers+=("${LAYER_ARNS[$layer_type]}")
    done
    
    layer_args=$(IFS=' '; echo "${deployed_layers[*]}")
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

    aws lambda invoke \
        --function-name "$FUNCTION_NAME" \
        --payload '{}' \
        --cli-binary-format raw-in-base64-out \
        --region $AWS_REGION \
        response.json &>/dev/null

    if [[ -f "response.json" ]]; then
        if grep -q '"errorMessage"' response.json 2>/dev/null; then
            echo "❌ 배치 실행 실패"
            cat response.json
            rm -f response.json
            exit 1
        else
            echo "✅ 배치 실행 완료"
            rm -f response.json
        fi
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
    cat > "layer-arns-${ENVIRONMENT}.json" << EOF
{
  "environment": "$ENVIRONMENT",
  "region": "$AWS_REGION",
  "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "layers": {
$(for layer_type in "${!LAYER_ARNS[@]}"; do
    echo "    \"${layer_type}\": \"${LAYER_ARNS[$layer_type]}\""
    [[ "$layer_type" != "${!LAYER_ARNS[@]: -1}" ]] && echo ","
  done)
  }
}
EOF
    echo "📄 Layer ARN 정보가 layer-arns-${ENVIRONMENT}.json에 저장되었습니다."
fi
