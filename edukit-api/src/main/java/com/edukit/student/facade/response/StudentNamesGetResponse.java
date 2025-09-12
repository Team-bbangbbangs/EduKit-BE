package com.edukit.student.facade.response;

import com.edukit.core.student.service.dto.StudentNameItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StudentNamesGetResponse(
        @Schema(description = "학년 목록")
        List<Integer> grades,
        @Schema(description = "반 목록")
        List<Integer> classNumbers,
        @Schema(description = "학생 목록")
        List<StudentNamesGetResponseItem> studentNames
) {
    public static StudentNamesGetResponse of(final List<Integer> grades, final List<Integer> classNumbers,
                                             final List<StudentNameItem> studentNameItems) {
        return new StudentNamesGetResponse(grades, classNumbers,
                studentNameItems.stream().map(StudentNamesGetResponseItem::of).toList());
    }

    public record StudentNamesGetResponseItem(
            @Schema(description = "생활기록부 ID", example = "1")
            long recordId,

            @Schema(description = "학생 이름", example = "홍길동")
            String studentName
    ) {
        public StudentNamesGetResponseItem(final long recordId, final String studentName) {
            this.recordId = recordId;
            this.studentName = studentName;
        }

        public static StudentNamesGetResponseItem of(final StudentNameItem studentNameItem) {
            return new StudentNamesGetResponseItem(studentNameItem.recordId(), studentNameItem.studentName());
        }
    }
}
