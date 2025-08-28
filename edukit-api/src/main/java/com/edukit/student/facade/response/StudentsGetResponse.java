package com.edukit.student.facade.response;

import com.edukit.core.student.service.dto.StudentItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StudentsGetResponse(
        @Schema(description = "총 등록된 학생 수")
        int studentCount,
        @Schema(description = "학생 목록")
        List<StudentsGetResponseItem> students
) {
    public static StudentsGetResponse of(final int studentCount, final List<StudentItem> studentItems) {
        return new StudentsGetResponse(studentCount, studentItems.stream().map(StudentsGetResponseItem::of).toList());
    }

    public record StudentsGetResponseItem(
            @Schema(description = "학생 ID", example = "1")
            long studentId,

            @Schema(description = "학생 학년", example = "2")
            int grade,

            @Schema(description = "학생 반 번호", example = "3")
            int classNumber,

            @Schema(description = "학생 번호", example = "15")
            int studentNumber,

            @Schema(description = "학생 이름", example = "홍길동")
            String studentName,

            @Schema(description = "생활기록부 관리 항목", example = "[\"세부능력 및 특기사항\", \"행동특성 및 종합의견\"]")
            List<String> recordTypes
    ) {
        public StudentsGetResponseItem(final long studentId, final int grade, final int classNumber,
                                       final int studentNumber,
                                       final String studentName,
                                       final List<String> recordTypes) {
            this.studentId = studentId;
            this.grade = grade;
            this.classNumber = classNumber;
            this.studentNumber = studentNumber;
            this.studentName = studentName;
            this.recordTypes = recordTypes;
        }

        public static StudentsGetResponseItem of(final StudentItem studentItem) {
            return new StudentsGetResponseItem(studentItem.studentId(), studentItem.grade(), studentItem.classNumber(),
                    studentItem.studentNumber(), studentItem.studentName(), studentItem.recordTypes());
        }
    }
}
