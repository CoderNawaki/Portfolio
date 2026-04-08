package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class ContactServiceTest {

    private ContactSubmissionRepository contactSubmissionRepository;

    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactSubmissionRepository = Mockito.mock(ContactSubmissionRepository.class);
        contactService = new ContactService(contactSubmissionRepository);
    }

    @Test
    void shouldPersistSubmissionWithNewStatus() {
        when(contactSubmissionRepository.save(any(ContactSubmission.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContactForm contactForm = new ContactForm();
        contactForm.setName("Lama");
        contactForm.setEmail("lama@example.com");
        contactForm.setMessage("I would like to discuss a full stack role.");

        ContactSubmissionResult result = contactService.submit(contactForm);

        ArgumentCaptor<ContactSubmission> submissionCaptor = ArgumentCaptor.forClass(ContactSubmission.class);
        verify(contactSubmissionRepository).save(submissionCaptor.capture());

        ContactSubmission savedSubmission = submissionCaptor.getValue();
        assertThat(savedSubmission.getName()).isEqualTo("Lama");
        assertThat(savedSubmission.getEmail()).isEqualTo("lama@example.com");
        assertThat(savedSubmission.getMessage()).isEqualTo("I would like to discuss a full stack role.");
        assertThat(savedSubmission.getStatus()).isEqualTo(ContactSubmissionStatus.NEW);
        assertThat(savedSubmission.getCreatedAt()).isNotNull();
        assertThat(savedSubmission.getCreatedAt()).isBeforeOrEqualTo(Instant.now());

        assertThat(result.message()).isEqualTo("Thanks, your message has been received. I will get back to you soon.");
        assertThat(result.submittedName()).isEqualTo("Lama");
    }
}
