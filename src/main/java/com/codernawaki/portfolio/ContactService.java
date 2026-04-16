package com.codernawaki.portfolio;


import io.micrometer.core.instrument.Timer;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<ContactSubmission> findAllSubmissions() {
        return contactSubmissionRepository.findAllByOrderByCreatedAtDesc();
    }

    public void updateSubmission(long submissionId, UpdateContactSubmissionForm updateForm) {
        ContactSubmission submission = contactSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found."));

        submission.setStatus(updateForm.getStatus());
        submission.setAdminNote(normalizeNote(updateForm.getAdminNote()));
        contactSubmissionRepository.save(submission);
        portfolioMetrics.recordAdminSubmissionUpdate(updateForm.getStatus().name());
    }

    private String normalizeNote(String adminNote) {
        if (adminNote == null || adminNote.isBlank()) {
            return null;
        }

        return adminNote.trim();
    }
}
