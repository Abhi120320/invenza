package com.invenza.service.impl;

import com.invenza.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String fromName;
    private final String appEnv;
    private final String smtpUser;

    public SmtpEmailService(
            JavaMailSender mailSender,
            @Value("${app.emails.from-email:noreply@invenza.com}") String fromEmail,
            @Value("${app.emails.from-name:Invenza}") String fromName,
            @Value("${app.env:development}") String appEnv,
            @Value("${spring.mail.username:}") String smtpUser) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.appEnv = appEnv;
        this.smtpUser = smtpUser;
    }

    @Override
    public boolean isDevMode() {
        return "development".equalsIgnoreCase(appEnv);
    }

    @Override
    public void sendOtpEmail(String toEmail, String code) {
        if (smtpUser == null || smtpUser.isBlank()) {
            log.info("[DEV MODE] Skipping actual email send. Generated OTP for {}: {}", toEmail, code);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(toEmail);
            helper.setSubject("Your Invenza OTP Code");

            String htmlBody = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto; padding: 24px;
                            background: #0f1117; color: #e8eaf0; border-radius: 12px;">
                  <h2 style="color: #6c7ffc; margin-bottom: 8px;">Invenza Verification Code</h2>
                  <p style="color: #9ca3b0; margin-bottom: 24px;">Use the code below to complete your verification. It expires in 10 minutes.</p>
                  <div style="text-align: center; padding: 20px; background: #1a1f2e; border-radius: 10px;
                              font-size: 2.5rem; font-weight: 800; letter-spacing: 0.4em; color: #fff;">
                    %s
                  </div>
                  <p style="color: #6b7280; font-size: 0.8rem; margin-top: 24px;">
                    If you did not request this, ignore this email. Your account is safe.
                  </p>
                </div>
            """, code);

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Successfully sent OTP email to {}", toEmail);
        } catch (Exception e) {
            log.error("[SMTP ERROR] Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }
}

