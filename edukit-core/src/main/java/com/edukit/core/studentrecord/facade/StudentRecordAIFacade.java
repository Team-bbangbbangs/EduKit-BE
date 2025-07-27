package com.edukit.core.studentrecord.facade;

import com.edukit.core.member.entity.Member;
import com.edukit.core.member.service.MemberService;
import com.edukit.core.studentrecord.entity.StudentRecord;
import com.edukit.core.studentrecord.facade.response.StudentRecordTaskResponse;
import com.edukit.core.studentrecord.service.StudentRecordService;
import com.edukit.core.studentrecord.util.AIPromptGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentRecordAIFacade {

    private final MemberService memberService;
    private final StudentRecordService studentRecordService;

    @Transactional
    public StudentRecordTaskResponse getPrompt(final long memberId, final long recordId, final int byteCount,
                                               final String userPrompt) {
        Member member = memberService.getMemberById(memberId);
        StudentRecord studentRecord = studentRecordService.getRecordDetail(member.getId(), recordId);

        String requestPrompt = AIPromptGenerator.createPrompt(studentRecord.getStudentRecordType(), byteCount, userPrompt);
        long taskId = studentRecordService.createAITask(studentRecord, userPrompt);
        return StudentRecordTaskResponse.of(taskId, requestPrompt);
    }
}
