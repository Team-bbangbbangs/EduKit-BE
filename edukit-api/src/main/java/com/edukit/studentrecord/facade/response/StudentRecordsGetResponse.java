package com.edukit.studentrecord.facade.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StudentRecordsGetResponse(
        @Schema(description = "생활기록부 목록")
        List<StudentRecordItems> studentRecords
) {
    public static StudentRecordsGetResponse of(final List<StudentRecordItems> studentRecordItems) {
        return new StudentRecordsGetResponse(studentRecordItems);
    }
    
    public record StudentRecordItems(
            @Schema(description = "생활기록부 ID", example = "1")
            long recordId,
            
            @Schema(description = "학생 학년", example = "2")
            int grade,
            
            @Schema(description = "학생 반 번호", example = "3")
            int classNumber,
            
            @Schema(description = "학생 번호", example = "15")
            int studentNumber,
            
            @Schema(description = "학생 이름", example = "홍길동")
            String studentName,
            
            @Schema(description = "생활기록부 내용", example = "수학 수업에 적극적으로 참여하며, 어려운 문제도 포기하지 않고 끝까지 노력하는 모습을 보인다.")
            String description
    ) {
        public static StudentRecordItems of(final long recordId,
                                            final int grade,
                                            final int classNumber,
                                            final int studentNumber,
                                            final String studentName,
                                            final String description) {
            return new StudentRecordItems(recordId, grade, classNumber, studentNumber, studentName, description);
        }
    }
}
