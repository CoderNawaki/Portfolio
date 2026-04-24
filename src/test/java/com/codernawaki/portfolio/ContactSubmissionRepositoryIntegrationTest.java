package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest(properties = {
        "PORTFOLIO_ADMIN_USERNAME=test-admin",
        "PORTFOLIO_ADMIN_PASSWORD=test-password"
})
class ContactSubmissionRepositoryIntegrationTest {

    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;

    @BeforeEach
    void setUp() {
        contactSubmissionRepository.deleteAll();
    }

    @Test
    void shouldSearchAcrossAdminNoteWithStatusFilter() {
        ContactSubmission matchingSubmission = submission(
                "Lama",
                "lama@example.com",
                "Interested in a backend role.",
                "Send interview slots.",
                ContactSubmissionStatus.REVIEWED,
                Instant.parse("2026-04-20T10:00:00Z"));
        ContactSubmission wrongStatus = submission(
                "Lama",
                "lama@example.com",
                "Interested in a backend role.",
                "Send interview slots.",
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T11:00:00Z"));
        ContactSubmission noMatch = submission(
                "Another Candidate",
                "another@example.com",
                "Question about the stack.",
                "Asked for architecture notes.",
                ContactSubmissionStatus.REVIEWED,
                Instant.parse("2026-04-20T12:00:00Z"));

        contactSubmissionRepository.saveAll(List.of(matchingSubmission, wrongStatus, noMatch));

        Page<ContactSubmission> result = contactSubmissionRepository.search(
                "%interview%",
                ContactSubmissionStatus.REVIEWED,
                PageRequest.of(0, 10, AdminSubmissionSort.NEWEST.toSort()));

        assertThat(result.getContent())
                .extracting(ContactSubmission::getEmail)
                .containsExactly("lama@example.com");
    }

    @Test
    void shouldApplyRequestedSortFromPageable() {
        ContactSubmission zed = submission(
                "Zed",
                "zed@example.com",
                "First message.",
                null,
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T10:00:00Z"));
        ContactSubmission anna = submission(
                "Anna",
                "anna@example.com",
                "Second message.",
                null,
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T11:00:00Z"));
        ContactSubmission mike = submission(
                "Mike",
                "mike@example.com",
                "Third message.",
                null,
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T12:00:00Z"));

        contactSubmissionRepository.saveAll(List.of(zed, anna, mike));

        Page<ContactSubmission> result = contactSubmissionRepository.search(
                null,
                null,
                PageRequest.of(0, 10, AdminSubmissionSort.NAME.toSort()));

        assertThat(result.getContent())
                .extracting(ContactSubmission::getName)
                .containsExactly("Anna", "Mike", "Zed");
    }

    @Test
    void shouldApplyRequestedPagingAndOldestSort() {
        ContactSubmission first = submission(
                "First",
                "first@example.com",
                "First message.",
                null,
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T10:00:00Z"));
        ContactSubmission second = submission(
                "Second",
                "second@example.com",
                "Second message.",
                null,
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T11:00:00Z"));
        ContactSubmission third = submission(
                "Third",
                "third@example.com",
                "Third message.",
                null,
                ContactSubmissionStatus.NEW,
                Instant.parse("2026-04-20T12:00:00Z"));

        contactSubmissionRepository.saveAll(List.of(first, second, third));

        Page<ContactSubmission> result = contactSubmissionRepository.search(
                null,
                null,
                PageRequest.of(1, 1, AdminSubmissionSort.OLDEST.toSort()));

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent())
                .extracting(ContactSubmission::getName)
                .containsExactly("Second");
    }

    private ContactSubmission submission(String name,
                                         String email,
                                         String message,
                                         String adminNote,
                                         ContactSubmissionStatus status,
                                         Instant createdAt) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(name);
        submission.setEmail(email);
        submission.setMessage(message);
        submission.setAdminNote(adminNote);
        submission.setStatus(status);
        submission.setCreatedAt(createdAt);
        return submission;
    }
}
