package com.edukit.core.common.service;

public interface EmailService {

    void sendEmail(final String emailReceiver, final String memberUuid, final String verificationCode);
}
