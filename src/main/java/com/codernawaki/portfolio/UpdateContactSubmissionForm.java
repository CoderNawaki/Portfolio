package com.codernawaki.portfolio;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateContactSubmissionForm {

    @NotNull
    private ContactSubmissionStatus status;

    @Size(max = 1200, message = "Admin note must be 1200 characters or fewer.")
    private String adminNote;

    public ContactSubmissionStatus getStatus() {
        return status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setStatus(ContactSubmissionStatus status) {
        this.status = status;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote == null ? null : adminNote.trim();
    }
}
