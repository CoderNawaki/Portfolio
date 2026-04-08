package com.codernawaki.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final ContactService contactService;

    public AdminController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/admin/contact-submissions")
    public String contactSubmissions(Model model) {
        model.addAttribute("submissions", contactService.findAllSubmissions());
        return "admin/contact-submissions";
    }
}
