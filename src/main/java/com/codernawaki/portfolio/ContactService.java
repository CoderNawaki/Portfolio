package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactSubmissionRepository contactSubmissionRepository;

    public ContactService(ContactSubmissionRepository contactSubmissionRepository) {
        this.contactSubmissionRepository = contactSubmissionRepository;
    }

    public ContactSubmissionResult submit(ContactForm contactForm) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(contactForm.getName());
        submission.setEmail(contactForm.getEmail());
        submission.setMessage(contactForm.getMessage());
        submission.setCreatedAt(Instant.now());
        submission.setStatus(ContactSubmissionStatus.NEW);

        contactSubmissionRepository.save(submission);

        return new ContactSubmissionResult(
                "Thanks, your message has been received. I will get back to you soon.",
                contactForm.getName());
    }

    public List<ContactSubmission> findAllSubmissions() {
        return contactSubmissionRepository.findAllByOrderByCreatedAtDesc();
    }
}
