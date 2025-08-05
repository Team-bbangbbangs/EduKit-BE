package com.edukit.core.common.service;

public interface EmailService {

    void sendEmail(String emailReceiver, String memberUuid, String verificationCode);
}
