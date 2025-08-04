package com.edukit.api.studentrecord.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student-records")
@RequiredArgsConstructor
public class StudentRecordController {

    // CRUD 전용 -> AI 기반 생성 전용은 따로 Controller와 Service로 분리하여 책임 명확히
}
