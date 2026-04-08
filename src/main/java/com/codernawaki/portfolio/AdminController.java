package com.codernawaki.portfolio;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final ContactService contactService;

    public AdminController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/admin/contact-submissions")
    public String contactSubmissions(Model model) {
        model.addAttribute("submissions", contactService.findAllSubmissions());
        model.addAttribute("availableStatuses", ContactSubmissionStatus.values());
        return "admin/contact-submissions";
    }

    @PostMapping("/admin/contact-submissions/{submissionId}")
    public String updateContactSubmission(
            @PathVariable long submissionId,
            @Valid @ModelAttribute UpdateContactSubmissionForm updateForm,
            RedirectAttributes redirectAttributes) {
        contactService.updateSubmission(submissionId, updateForm);
        redirectAttributes.addFlashAttribute("adminMessage", "Submission updated.");
        return "redirect:/admin/contact-submissions";
    }
}
