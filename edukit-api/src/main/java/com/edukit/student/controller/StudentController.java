package com.edukit.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/student")
@RequiredArgsConstructor
public class StudentController {

    @PostMapping
    public ResponseEntity<Void> createStudent() {

        return ResponseEntity.ok().build();
    }
}
