package com.codernawaki.portfolio;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private static final int SUBMISSIONS_PER_PAGE = 10;

    private final ContactService contactService;

    public AdminController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/admin/contact-submissions")
    public String contactSubmissions(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        int safePage = Math.max(page, 0);
        Page<ContactSubmission> submissionsPage = contactService.findSubmissions(
                query, status, PageRequest.of(safePage, SUBMISSIONS_PER_PAGE));

        model.addAttribute("submissionsPage", submissionsPage);
        model.addAttribute("submissions", submissionsPage.getContent());
        model.addAttribute("availableStatuses", ContactSubmissionStatus.values());
        model.addAttribute("currentQuery", query);
        model.addAttribute("currentStatus", status == null ? "" : status.name());
        return "admin/contact-submissions";
    }

    @PostMapping("/admin/contact-submissions/{submissionId}")
    public String updateContactSubmission(
            @PathVariable long submissionId,
            @Valid @ModelAttribute UpdateContactSubmissionForm updateForm,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(name = "filterStatus", required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        contactService.updateSubmission(submissionId, updateForm);
        redirectAttributes.addFlashAttribute("adminMessage", "Submission updated.");
        populateRedirectState(redirectAttributes, query, status, page);
        return "redirect:/admin/contact-submissions";
    }

    @PostMapping("/admin/contact-submissions/{submissionId}/delete")
    public String deleteContactSubmission(
            @PathVariable long submissionId,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(name = "filterStatus", required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        contactService.deleteSubmission(submissionId);
        redirectAttributes.addFlashAttribute("adminMessage", "Submission deleted.");
        populateRedirectState(redirectAttributes, query, status, page);
        return "redirect:/admin/contact-submissions";
    }

    private void populateRedirectState(RedirectAttributes redirectAttributes,
                                       String query,
                                       ContactSubmissionStatus status,
                                       int page) {
        if (query != null && !query.isBlank()) {
            redirectAttributes.addAttribute("query", query);
        }
        if (status != null) {
            redirectAttributes.addAttribute("status", status);
        }
        redirectAttributes.addAttribute("page", Math.max(page, 0));
    }
}
