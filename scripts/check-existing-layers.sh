#!/bin/bash

echo "🔍 기존 Lambda Layer 확인 중..."

ENVIRONMENT="${1:-dev}"
AWS_REGION="${AWS_REGION:-ap-northeast-2}"

layers=("spring" "database" "external" "utils")

for layer_type in "${layers[@]}"; do
    layer_name="edukit-${layer_type}-layer-${ENVIRONMENT}"
    echo ""
    echo "📦 ${layer_name} 확인 중..."
    
    if aws lambda list-layer-versions --layer-name "$layer_name" --region "$AWS_REGION" --max-items 5 2>/dev/null; then
        echo "✅ Layer가 존재합니다"
        
        echo "기존 Layer를 삭제하시겠습니까? (y/N)"
        read -r confirm
        if [[ $confirm == "y" || $confirm == "Y" ]]; then
            echo "🗑️ Layer 버전들 삭제 중..."
            aws lambda list-layer-versions --layer-name "$layer_name" --region "$AWS_REGION" --query 'LayerVersions[].Version' --output text | \
            while read version; do
                if [[ -n "$version" && "$version" != "None" ]]; then
                    echo "  삭제 중: 버전 $version"
                    aws lambda delete-layer-version --layer-name "$layer_name" --version-number "$version" --region "$AWS_REGION" 2>/dev/null || true
                fi
            done
        fi
    else
        echo "ℹ️ Layer가 존재하지 않습니다 (정상)"
    fi
done

echo ""
echo "✅ Layer 확인 완료"