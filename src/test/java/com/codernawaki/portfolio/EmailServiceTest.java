package com.codernawaki.portfolio;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = Mockito.mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void shouldSkipSendingWhenMailCredentialsAreMissing() {
        ReflectionTestUtils.setField(emailService, "notificationEmail", "owner@example.com");
        ReflectionTestUtils.setField(emailService, "mailUsername", "");
        ReflectionTestUtils.setField(emailService, "mailPassword", "");

        emailService.sendContactNotification(buildSubmission());

        verify(mailSender, never()).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void shouldSendUsingConfiguredMailboxAndReplyToSubmitter() {
        ReflectionTestUtils.setField(emailService, "notificationEmail", "owner@example.com");
        ReflectionTestUtils.setField(emailService, "mailUsername", "smtp-user@example.com");
        ReflectionTestUtils.setField(emailService, "mailPassword", "app-password");

        ContactSubmission submission = buildSubmission();

        emailService.sendContactNotification(submission);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        org.assertj.core.api.Assertions.assertThat(message.getFrom()).isEqualTo("smtp-user@example.com");
        org.assertj.core.api.Assertions.assertThat(message.getReplyTo()).isEqualTo("lama@example.com");
        org.assertj.core.api.Assertions.assertThat(message.getTo()).containsExactly("owner@example.com");
    }

    private ContactSubmission buildSubmission() {
        ContactSubmission submission = new ContactSubmission();
        submission.setName("Lama");
        submission.setEmail("lama@example.com");
        submission.setMessage("Interested in discussing a role.");
        return submission;
    }
}
