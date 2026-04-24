package com.codernawaki.portfolio;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/admin/contact-submissions/export")
    public ResponseEntity<String> exportContactSubmissions(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "NEWEST") AdminSubmissionSort sort) {
        String csv = contactService.exportSubmissionsAsCsv(query, status, sort.toSort());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contact-submissions.csv\"")
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }

    @GetMapping("/admin/contact-submissions")
    public String contactSubmissions(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "NEWEST") AdminSubmissionSort sort,
            @RequestParam(required = false) Long selected,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        int safePage = Math.max(page, 0);
        Page<ContactSubmission> submissionsPage = contactService.findSubmissions(
                query, status, PageRequest.of(safePage, SUBMISSIONS_PER_PAGE, sort.toSort()));

        model.addAttribute("submissionsPage", submissionsPage);
        model.addAttribute("submissions", submissionsPage.getContent());
        model.addAttribute("availableStatuses", ContactSubmissionStatus.values());
        model.addAttribute("availableSorts", AdminSubmissionSort.values());
        model.addAttribute("currentQuery", query);
        model.addAttribute("currentStatus", status == null ? "" : status.name());
        model.addAttribute("currentSort", sort.name());
        model.addAttribute("selectedSubmission", resolveSelectedSubmission(selected, submissionsPage));
        return "admin/contact-submissions";
    }

    @PostMapping("/admin/contact-submissions/{submissionId}")
    public String updateContactSubmission(
            @PathVariable long submissionId,
            @Valid @ModelAttribute UpdateContactSubmissionForm updateForm,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(name = "filterStatus", required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "NEWEST") AdminSubmissionSort sort,
            @RequestParam(defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        contactService.updateSubmission(submissionId, updateForm);
        redirectAttributes.addFlashAttribute("adminMessage", "Submission updated.");
        populateRedirectState(redirectAttributes, query, status, sort, page);
        redirectAttributes.addAttribute("selected", submissionId);
        return "redirect:/admin/contact-submissions";
    }

    @PostMapping("/admin/contact-submissions/{submissionId}/delete")
    public String deleteContactSubmission(
            @PathVariable long submissionId,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(name = "filterStatus", required = false) ContactSubmissionStatus status,
            @RequestParam(defaultValue = "NEWEST") AdminSubmissionSort sort,
            @RequestParam(defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        contactService.deleteSubmission(submissionId);
        redirectAttributes.addFlashAttribute("adminMessage", "Submission deleted.");
        populateRedirectState(redirectAttributes, query, status, sort, page);
        return "redirect:/admin/contact-submissions";
    }

    private void populateRedirectState(RedirectAttributes redirectAttributes,
                                       String query,
                                       ContactSubmissionStatus status,
                                       AdminSubmissionSort sort,
                                       int page) {
        if (query != null && !query.isBlank()) {
            redirectAttributes.addAttribute("query", query);
        }
        if (status != null) {
            redirectAttributes.addAttribute("status", status);
        }
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("page", Math.max(page, 0));
    }

    private ContactSubmission resolveSelectedSubmission(Long selectedSubmissionId,
                                                        Page<ContactSubmission> submissionsPage) {
        if (selectedSubmissionId != null) {
            return contactService.findSubmission(selectedSubmissionId).orElse(null);
        }
        return submissionsPage.getContent().stream().findFirst().orElse(null);
    }
}
