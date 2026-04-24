package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ContactServiceTest {

    private ContactSubmissionRepository contactSubmissionRepository;
    private EmailService emailService;
    private PortfolioMetrics portfolioMetrics;
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactSubmissionRepository = Mockito.mock(ContactSubmissionRepository.class);
        emailService = Mockito.mock(EmailService.class);
        portfolioMetrics = Mockito.mock(PortfolioMetrics.class);
        contactService = new ContactService(contactSubmissionRepository, emailService, portfolioMetrics);
    }

    @Test
    void shouldPersistSubmissionAndSendNotification() {
        when(contactSubmissionRepository.existsByEmailIgnoreCaseAndMessageAndCreatedAtAfter(
                eq("lama@example.com"), eq("I would like to discuss a full stack role."), any(Instant.class)))
                .thenReturn(false);
        when(contactSubmissionRepository.save(any(ContactSubmission.class)))
                .thenAnswer(invocation -> {
                    ContactSubmission submission = invocation.getArgument(0);
                    submission.onCreate(); // Simulate @PrePersist
                    return submission;
                });

        ContactForm contactForm = new ContactForm();
        contactForm.setName("Lama");
        contactForm.setEmail("lama@example.com");
        contactForm.setMessage("I would like to discuss a full stack role.");

        ContactSubmissionResult result = contactService.submit(contactForm);

        ArgumentCaptor<ContactSubmission> submissionCaptor = ArgumentCaptor.forClass(ContactSubmission.class);
        verify(contactSubmissionRepository).save(submissionCaptor.capture());
        
        ContactSubmission savedSubmission = submissionCaptor.getValue();
        assertThat(savedSubmission.getName()).isEqualTo("Lama");
        assertThat(savedSubmission.getStatus()).isEqualTo(ContactSubmissionStatus.NEW);

        verify(emailService).sendContactNotification(savedSubmission);

        assertThat(result.message()).isEqualTo("Thanks, your message has been received. I will get back to you soon.");
        assertThat(result.submittedName()).isEqualTo("Lama");
    }

    @Test
    void shouldRejectDuplicateSubmissionWithinProtectionWindow() {
        ContactForm contactForm = new ContactForm();
        contactForm.setName("Lama");
        contactForm.setEmail("lama@example.com");
        contactForm.setMessage("I would like to discuss a full stack role.");

        when(contactSubmissionRepository.existsByEmailIgnoreCaseAndMessageAndCreatedAtAfter(
                eq("lama@example.com"), eq("I would like to discuss a full stack role."), any(Instant.class)))
                .thenReturn(true);

        ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class,
                () -> contactService.submit(contactForm));

        assertThat(exception.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(exception.getReason()).contains("already submitted recently");
        verify(contactSubmissionRepository, never()).save(any(ContactSubmission.class));
        verify(emailService, never()).sendContactNotification(any(ContactSubmission.class));
    }

    @Test
    void shouldNormalizeQueryAndCallSearch() {
        Pageable pageable = PageRequest.of(0, 10, AdminSubmissionSort.NEWEST.toSort());
        when(contactSubmissionRepository.search(eq("%test%"), eq(ContactSubmissionStatus.NEW), eq(pageable)))
                .thenReturn(Page.empty());

        contactService.findSubmissions("  TEST  ", ContactSubmissionStatus.NEW, pageable);

        verify(contactSubmissionRepository).search(eq("%test%"), eq(ContactSubmissionStatus.NEW), eq(pageable));
    }

    @Test
    void shouldHandleNullQueryInSearch() {
        Pageable pageable = PageRequest.of(0, 10, AdminSubmissionSort.NEWEST.toSort());
        when(contactSubmissionRepository.search(eq(null), eq(null), eq(pageable)))
                .thenReturn(Page.empty());

        contactService.findSubmissions(null, null, pageable);

        verify(contactSubmissionRepository).search(eq(null), eq(null), eq(pageable));
    }
}
