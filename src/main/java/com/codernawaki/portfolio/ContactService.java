package com.codernawaki.portfolio;


import io.micrometer.core.instrument.Timer;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ContactService {

    private final ContactSubmissionRepository contactSubmissionRepository;
    private final EmailService emailService;
    private final PortfolioMetrics portfolioMetrics;

    public ContactService(ContactSubmissionRepository contactSubmissionRepository,
                          EmailService emailService,
                          PortfolioMetrics portfolioMetrics) {
        this.contactSubmissionRepository = contactSubmissionRepository;
        this.emailService = emailService;
        this.portfolioMetrics = portfolioMetrics;
    }

    public ContactSubmissionResult submit(ContactForm contactForm) {
        Timer.Sample sample = portfolioMetrics.startContactSubmission();
        String outcome = "failed";

        try {
            ContactSubmission submission = new ContactSubmission();
            submission.setName(contactForm.getName());
            submission.setEmail(contactForm.getEmail());
            submission.setMessage(contactForm.getMessage());

            ContactSubmission savedSubmission = contactSubmissionRepository.save(submission);
            emailService.sendContactNotification(savedSubmission);

            outcome = "accepted";
            portfolioMetrics.recordContactSubmission(outcome);
            return new ContactSubmissionResult(
                    "Thanks, your message has been received. I will get back to you soon.",
                    contactForm.getName());
        } finally {
            portfolioMetrics.stopContactSubmission(sample, outcome);
        }
    }

    public Page<ContactSubmission> findSubmissions(String query,
                                                   ContactSubmissionStatus status,
                                                   Pageable pageable) {
        String normalized = normalizeQuery(query);
        String searchPattern = normalized != null ? "%" + normalized.toLowerCase() + "%" : null;
        return contactSubmissionRepository.search(searchPattern, status, pageable);
    }

    public Optional<ContactSubmission> findSubmission(long submissionId) {
        return contactSubmissionRepository.findById(submissionId);
    }

    public void updateSubmission(long submissionId, UpdateContactSubmissionForm updateForm) {
        ContactSubmission submission = contactSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found."));

        submission.setStatus(updateForm.getStatus());
        submission.setAdminNote(normalizeNote(updateForm.getAdminNote()));
        contactSubmissionRepository.save(submission);
        portfolioMetrics.recordAdminSubmissionUpdate(updateForm.getStatus().name());
    }

    public void deleteSubmission(long submissionId) {
        ContactSubmission submission = contactSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found."));
        contactSubmissionRepository.delete(submission);
    }

    private String normalizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        return query.trim();
    }

    private String normalizeNote(String adminNote) {
        if (adminNote == null || adminNote.isBlank()) {
            return null;
        }

        return adminNote.trim();
    }
}
