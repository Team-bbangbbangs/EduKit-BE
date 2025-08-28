package com.edukit.core.common.service;

import com.edukit.core.common.event.mail.EmailTemplate;

public interface EmailService {

    void sendEmail(String emailReceiver, String memberUuid, String verificationCode, EmailTemplate emailTemplate);
}
