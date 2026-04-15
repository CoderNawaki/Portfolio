package com.codernawaki.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${portfolio.contact.notification-email}")
    private String notificationEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendContactNotification(ContactSubmission submission) {
        try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setFrom(notificationEmail);
                        message.setTo(notificationEmail);
                        message.setSubject("New Portfolio Contact Submission from " + submission.getName());
                        message.setText(String.format("""
                                You have received a new contact submission:
            
                                Name: %s
                                Email: %s
            
                                Message:
                                %s
            
                                View it here: http://localhost:8081/admin/contact-submissions""",
                                submission.getName(), submission.getEmail(), submission.getMessage()));

            mailSender.send(message);
            logger.info("Successfully sent contact notification for submission from: {}", submission.getName());
        } catch (Exception e) {
            logger.error("Failed to send contact notification email for submission from: {}", submission.getName(), e);
        }
    }
}

