package com.edukit.core.common.port;

public interface EmailService {

    void sendEmail(String emailReceiver, String memberUuid, String verificationCode);
}
