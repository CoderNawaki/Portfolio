package com.codernawaki.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${portfolio.contact.notification-email}")
    private String notificationEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendContactNotification(ContactSubmission submission) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationEmail);
        message.setSubject("New Portfolio Contact Submission from " + submission.getName());
        message.setText(String.format(
                "You have received a new contact submission:\n\n" +
                "Name: %s\n" +
                "Email: %s\n\n" +
                "Message:\n%s\n\n" +
                "View it here: http://localhost:8081/admin/contact-submissions",
                submission.getName(), submission.getEmail(), submission.getMessage()));
        
        mailSender.send(message);
    }
}
