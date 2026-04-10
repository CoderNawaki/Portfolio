package com.codernawaki.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PortfolioService portfolioService;

    public HomeController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/")
    public String home(Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        
        model.addAttribute("role", props.getRole());
        model.addAttribute("location", props.getLocation());
        model.addAttribute("resumeLabel", props.getResumeLabel());
        model.addAttribute("availability", props.getAvailability());
        model.addAttribute("focusAreas", props.getFocusAreas());
        model.addAttribute("proofPoints", props.getProofPoints());
        model.addAttribute("bio", props.getBio());
        model.addAttribute("projects", portfolioService.getFeaturedProjects());
        
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
