package com.edukit.core.studentrecord.service;

import com.edukit.core.studentrecord.db.entity.StudentRecordAITask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AITaskService {

    @Transactional
    public void startTask(final StudentRecordAITask task) {
        task.start();
    }
}
