package com.statestreet.resourceallocation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String FROM = "pardhasaradhi.gavaraa@gmail.com";

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendProfileLink(String toEmail, String employeeName, String token, String projectName) {
        String profileUrl = frontendUrl + "/employee/profile?token=" + token;

        // Always log the link so it's visible in console for testing
        log.info("========== PROFILE LINK ==========");
        log.info("Employee: {} ({})", employeeName, toEmail);
        log.info("Project: {}", projectName);
        log.info("Link: {}", profileUrl);
        log.info("==================================");

        String subject = "Action Required: Complete Your Profile — " + projectName;

        String body =
                "<p>Hello " + employeeName + ",</p>"
              + "<p>You have been allocated to the project <strong>" + projectName + "</strong>.</p>"
              + "<p>Please click the link below to complete your profile:</p>"
              + "<p><a href=\"" + profileUrl + "\" style=\"color:#1565c0;font-weight:bold;\">Click here to complete your profile</a></p>"
              + "<p>&#9888;&#65039; This link is personal, expires in 48 hours, and can only be used once.</p>"
              + "<p>If you did not request this, please ignore this email.</p>"
              + "<p>Regards,<br/>Resource Allocation Team</p>";

        sendHtmlEmail(toEmail, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(FROM);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            log.info("Email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {} : {}", to, e.getMessage());
        }
    }
}
