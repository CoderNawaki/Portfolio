package com.codernawaki.portfolio;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactForm {

    @NotBlank(message = "Please enter your name.")
    @Size(max = 100, message = "Name must be 100 characters or fewer.")
    private String name;

    @NotBlank(message = "Please enter your email.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 150, message = "Email must be 150 characters or fewer.")
    private String email;

    @NotBlank(message = "Please enter a message.")
    @Size(max = 2000, message = "Message must be 2000 characters or fewer.")
    private String message;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = normalize(name);
    }

    public void setEmail(String email) {
        this.email = normalize(email);
    }

    public void setMessage(String message) {
        this.message = normalize(message);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
