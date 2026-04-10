package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {

    private final ContactSubmissionRepository contactSubmissionRepository;
    private final EmailService emailService;

    public ContactService(ContactSubmissionRepository contactSubmissionRepository, EmailService emailService) {
        this.contactSubmissionRepository = contactSubmissionRepository;
        this.emailService = emailService;
    }

    public ContactSubmissionResult submit(ContactForm contactForm) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(contactForm.getName());
        submission.setEmail(contactForm.getEmail());
        submission.setMessage(contactForm.getMessage());

        ContactSubmission savedSubmission = contactSubmissionRepository.save(submission);
        emailService.sendContactNotification(savedSubmission);

        return new ContactSubmissionResult(
                "Thanks, your message has been received. I will get back to you soon.",
                contactForm.getName());
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
    }

    private String normalizeNote(String adminNote) {
        if (adminNote == null || adminNote.isBlank()) {
            return null;
        }

        return adminNote.trim();
    }
}
