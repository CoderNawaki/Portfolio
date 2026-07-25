

package com.codernawaki.portfolio;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ContactController {

    private static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    @PostMapping("/submitContactForm")
    public ResponseEntity<Map<String, String>> submitContactForm(@RequestBody ContactForm contactForm) {

        if (isValid(contactForm)) {
            return ResponseEntity.ok(Map.of("message", "Form submitted successfully."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Invalid form data. Please check your input."));
    }

    private boolean isValid(ContactForm contactForm) {
        return contactForm != null
                && contactForm.getName() != null
                && !contactForm.getName().trim().isEmpty()
                && contactForm.getEmail() != null
                && contactForm.getEmail().matches(EMAIL_PATTERN)
                && contactForm.getMessage() != null
                && !contactForm.getMessage().trim().isEmpty();
    }
}
