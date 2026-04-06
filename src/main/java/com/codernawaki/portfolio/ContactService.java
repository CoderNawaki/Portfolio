package com.codernawaki.portfolio;

import org.springframework.stereotype.Service;

@Service
public class ContactService {

    public ContactSubmissionResult submit(ContactForm contactForm) {
        return new ContactSubmissionResult(
                "Thanks, your message has been received. I will get back to you soon.",
                contactForm.getName());
    }
}
