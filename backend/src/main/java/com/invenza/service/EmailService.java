package com.invenza.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String code);
    boolean isDevMode();
}

