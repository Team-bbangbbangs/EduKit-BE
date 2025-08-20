package com.edukit.core.studentrecord.service;

import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
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
    public void startTask(final StudentRecordAITask task) {
        task.start();
    }

    @Transactional
    public StudentRecordAITask createAITask(final String prompt) {
        StudentRecordAITask aiTask = StudentRecordAITask.create(prompt);
        aiTaskRepository.save(aiTask);
        return aiTask;
    }

    @Transactional
    public void completeAITask(final Long taskId) {
        StudentRecordAITask aiTask = aiTaskRepository.findById(taskId)
                .orElseThrow(() -> new StudentRecordException(StudentRecordErrorCode.AI_TASK_NOT_FOUND));
        aiTask.complete();
    }
}
