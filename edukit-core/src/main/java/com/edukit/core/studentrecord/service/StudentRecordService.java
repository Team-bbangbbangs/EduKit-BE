package com.edukit.core.studentrecord.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.student.db.entity.Student;
import com.edukit.core.student.utils.KoreanNormalizer;
import com.edukit.core.studentrecord.db.entity.StudentRecord;
import com.edukit.core.studentrecord.db.enums.StudentRecordType;
import com.edukit.core.studentrecord.db.repository.StudentRecordRepository;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentRecordService {

    private final StudentRecordRepository studentRecordRepository;

    @Transactional(readOnly = true)
    public StudentRecord getRecordDetail(final long memberId, final long recordId) {
        StudentRecord existingDetail = getRecordDetailById(recordId);
        validatePermission(existingDetail.getStudent(), memberId);
        return existingDetail;
    }

    @Transactional
    public void createStudentRecords(final Student student, final List<StudentRecordType> recordTypes) {
        validateRecordTypes(recordTypes);
        List<StudentRecord> studentRecords = recordTypes.stream()
                .map(recordType -> StudentRecord.create(student, recordType)).toList();
        studentRecordRepository.saveAll(studentRecords);
    }

    @Transactional(readOnly = true)
    public List<StudentRecord> getStudentRecordsByFilters(final Member member,
                                                          final StudentRecordType studentRecordType,
                                                          final Integer grade, final Integer classNumber,
                                                          final String search, final Long lastRecordId,
                                                          final int pageSize) {
        String searchNormalized = KoreanNormalizer.toNormalized(search);
        Pageable pageable = PageRequest.of(0, pageSize, Sort.unsorted());   //페이지 사이즈 용도

        if (lastRecordId == null) {
            return studentRecordRepository.findStudentRecordsByFilters(member, studentRecordType, grade, classNumber,
                    searchNormalized, search, pageable);
        }

        StudentRecord lastRecord = getRecordDetail(member.getId(), lastRecordId);
        Student lastStudent = lastRecord.getStudent();
        return studentRecordRepository.findStudentRecordsByFilters(member, studentRecordType, grade, classNumber,
                searchNormalized, search, lastRecord.getId(), lastStudent.getGrade(),
                lastStudent.getClassNumber(), lastStudent.getStudentNumber(), lastStudent.getStudentName(), pageable);
    }

    @Transactional
    public void updateStudentRecord(final StudentRecord studentRecord, final String description) {
        studentRecord.updateDescription(description);
    }

    @Transactional(readOnly = true)
    public List<StudentRecord> getAllStudentRecordsByType(final long memberId, final StudentRecordType recordType) {
        return studentRecordRepository.findByMemberIdAndStudentRecordType(memberId, recordType);
    }

    @Transactional(readOnly = true)
    public List<StudentRecord> getStudentRecordsByStudent(final Student student) {
        return studentRecordRepository.findAllByStudent(student);
    }

    @Transactional
    public void updateStudentRecord(final List<StudentRecordType> newTypes, final List<StudentRecord> existingRecords,
                                    final Student student) {
        validateRecordTypes(newTypes);
        createRecordsForNewTypes(newTypes, existingRecords, student);
        deleteRecordsForRemovedTypes(newTypes, existingRecords);
    }

    private StudentRecord getRecordDetailById(final long recordId) {
        return studentRecordRepository.findById(recordId)
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.AI));
    }

    private void validatePermission(final Student student, final long memberId) {
        if (student.getMember().getId() != memberId) {
            throw new StudentRecordException(StudentRecordErrorCode.PERMISSION_DENIED);
        }
    }

    private void validateRecordTypes(final List<StudentRecordType> recordTypes) {
        Set<StudentRecordType> uniqueTypes = new HashSet<>(recordTypes);
        if (uniqueTypes.size() != recordTypes.size()) {
            throw new StudentRecordException(StudentRecordErrorCode.DUPLICATE_RECORD_TYPE);
        }
    }

    private void createRecordsForNewTypes(final List<StudentRecordType> newTypes,
                                          final List<StudentRecord> existingRecords, final Student student) {
        List<StudentRecordType> existingTypes = existingRecords.stream()
                .map(StudentRecord::getStudentRecordType).toList();
        List<StudentRecord> recordsToCreate = newTypes.stream()
                .filter(it -> !existingTypes.contains(it))
                .map(it -> StudentRecord.create(student, it))
                .toList();

        if (!recordsToCreate.isEmpty()) {
            studentRecordRepository.saveAll(recordsToCreate);
        }
    }

    private void deleteRecordsForRemovedTypes(final List<StudentRecordType> newTypes,
                                              final List<StudentRecord> existingRecords) {
        List<Long> recordIds = existingRecords.stream()
                .filter(it -> !newTypes.contains(it.getStudentRecordType()))
                .map(StudentRecord::getId)
                .toList();

        if (!recordIds.isEmpty()) {
            studentRecordRepository.deleteAllByIdInBatch(recordIds);
        }
    }
}
