package com.edukit.core.studentrecord.util;

import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AIPromptGenerator {

    private static final String STREAMING_PROMPT_TEMPLATE = """
                        다음 지침에 따라 '%s' 항목의 생활기록부를 작성해주세요.
                        학생생활기록부 종합지원포털의 학생부 기재요령을 참고하여 기준에 맞게 작성해주세요.
            
                        ## 작성 내용
                        다음 정보를 바탕으로 구체적이고 상세한 생활기록부를 작성하세요: %s
                        ### 유의사항
                        입력된 정보가 부정적이더라도, 직접적이거나 간접적으로라도 부정적인 표현을 작성하면 안됩니다.
                        무엇을 배우고 성장했는지 최대한 긍정적인 관점이나 개선 방향을 서술해 주세요. '~할 계획임'과 같이 추측성 표현은 작성하지 마세요.
            
                        ## 중요: 작성 방식
                         각 버전은 반드시 전체 활동 내용을 포함하되, 서로 다른 관점과 초점을 가져야 합니다.
                        - 입력으로 들어온 전체 내용에 대해 처음부터 끝까지 모두 읽고 반영하여 각각 3가지의 서술 버전을 작성
                        - 각 버전은 반드시 입력값에 대한 전체의 내용을 포함하고 있어야 함. 절대 일부분만 발췌해서 작성하지 않음
                        - 같은 활동이라도 다른 관점에서 서술하고 다른 강조점과 표현 방식 사용
                        - 각 버전은 전체 내용을 포함하되 구성과 초점을 달리하여 작성
            
                        ## 매우 중요: 응답 형식
                        반드시 아래 형식으로만 응답하세요. 각 버전을 명확한 구분자로 구분해주세요:
            
                         ===VERSION_1===
                         (첫 번째 버전의 생활기록부 내용)
            
                         ===VERSION_2===
                         (두 번째 버전의 생활기록부 내용)
            
                         ===VERSION_3===
                         (세 번째 버전의 생활기록부 내용)
            
                         각 버전은 최소 %d바이트 이상 작성하고, 구분자(===VERSION_N===)는 반드시 포함해야 합니다.
                         중학교, 고등학교 생활기록부 글처럼 작성해주세요.
            """;

    private static final int MAXIMUM_BYTE_BUFFER = 100;


    public static String createStreamingPrompt(final StudentRecordType recordType, final int byteCount,
                                               final String inputPrompt) {
        return String.format(STREAMING_PROMPT_TEMPLATE, recordType.name(), inputPrompt,
                byteCount - MAXIMUM_BYTE_BUFFER);
    }
}
