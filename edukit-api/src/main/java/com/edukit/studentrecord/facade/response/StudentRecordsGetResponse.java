package com.edukit.studentrecord.facade.response;

import java.util.List;

public record StudentRecordsGetResponse(
        List<StudentRecordItems> studentRecords
) {
    public static StudentRecordsGetResponse of(final List<StudentRecordItems> studentRecordItems) {
        return new StudentRecordsGetResponse(studentRecordItems);
    }
    public record StudentRecordItems(
            long studentRecordId,
            int grade,
            int classNumber,
            int studentNumber,
            String studentName,
            String description
    ) {
        public static StudentRecordItems of(final long studentRecordId,
                                            final int grade,
                                            final int classNumber,
                                            final int studentNumber,
                                            final String studentName,
                                            final String description) {
            return new StudentRecordItems(studentRecordId, grade, classNumber, studentNumber, studentName, description);
        }
    }
}
