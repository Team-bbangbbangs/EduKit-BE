package com.edukit.core.studentrecord.util;

import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AIPromptGenerator {

    private static final String STREAMING_PROMPT_TEMPLATE = """
            주어진 학생의 활동 내용을 바탕으로 3가지 버전의 학교 학생 생활기록부 %s 항목의 초안을 만들어주세요.
            
            ## 작성 지침
            각 버전은 반드시 전체 활동 내용을 포함하되, 서로 다른 관점과 초점을 가져야 합니다.
            
            부정적인 내용이 있다면, 이를 통해 무엇을 배우고 성장했는지 긍정적인 관점이나 앞으로의 개선방향에 대해서 서술해 주세요. 단, '~할 계획임'과 같이 추측성 표현은 작성하지 마세요.
            
            지금은 바이트 수나 '음슴체', 금지 단어 같은 세세한 규칙은 신경 쓰지 말고 내용의 풍부함과 창의성에만 집중해 주세요.
            
            ## 학생 활동 내용
            %s
            
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
        return String.format(recordType.getDescription(), STREAMING_PROMPT_TEMPLATE, inputPrompt,
                byteCount - MAXIMUM_BYTE_BUFFER);
    }
}
