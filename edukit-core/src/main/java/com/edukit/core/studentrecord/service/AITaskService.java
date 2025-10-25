package com.edukit.core.studentrecord.service;

import com.edukit.core.member.db.entity.Member;
import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import com.edukit.core.studentrecord.db.enums.AIErrorType;
import com.edukit.core.studentrecord.db.repository.StudentRecordAITaskRepository;
import com.edukit.core.studentrecord.exception.StudentRecordErrorCode;
import com.edukit.core.studentrecord.exception.StudentRecordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AITaskService {

    private final StudentRecordAITaskRepository aiTaskRepository;

    @Transactional
    public StudentRecordAITask createAITask(final Member member, final String prompt) {
        StudentRecordAITask aiTask = StudentRecordAITask.create(member, prompt);
        aiTask.start();
        aiTaskRepository.save(aiTask);
        return aiTask;
    }

    @Transactional
    public void completeAITask(final Long taskId) {
        StudentRecordAITask aiTask = aiTaskRepository.findById(taskId)
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.AI_TASK_NOT_FOUND));
        aiTask.complete();
    }

    public void validateUserTask(final long memberId, final String taskId) {
        boolean exists = aiTaskRepository.existsByIdAndMemberId(parseTaskId(taskId), memberId);
        if (!exists) {
            throw new StudentRecordException(StudentRecordErrorCode.AI_TASK_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public StudentRecordAITask getTaskById(final Long taskId) {
        return aiTaskRepository.findById(taskId)
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.AI_TASK_NOT_FOUND));
    }

    @Transactional
    public void markTaskAsFailed(final Long taskId, final String errorType) {
        StudentRecordAITask task = aiTaskRepository.findById(taskId)
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.AI_TASK_NOT_FOUND));
        AIErrorType aiErrorType = AIErrorType.fromString(errorType);
        task.markAsFailed(aiErrorType);
    }

    private long parseTaskId(final String taskId) {
        try {
            return Long.parseLong(taskId);
        } catch (NumberFormatException e) {
            throw new StudentRecordException(StudentRecordErrorCode.NUMBER_FORMAT_EXCEPTION);
        }
    }
}
