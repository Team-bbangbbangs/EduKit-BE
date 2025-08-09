package com.edukit.core.common.service.port;

public interface EmailService {

    void sendEmail(String emailReceiver, String memberUuid, String verificationCode);
}
