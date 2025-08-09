package com.edukit.student.facade;

import com.edukit.core.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentFacade {

    private final StudentService studentService;
}
