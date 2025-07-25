package com.edukit.core.subject.service;

import com.edukit.core.subject.entity.Subject;
import com.edukit.core.subject.exception.SubjectException;
import com.edukit.core.subject.exception.SubjectErrorCode;
import com.edukit.core.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public Subject getSubjectByName(final String subjectName) {
        return subjectRepository.findByName(subjectName)
                .orElseThrow(() -> new SubjectException(SubjectErrorCode.SUBJECT_NOT_FOUND));
    }
}
