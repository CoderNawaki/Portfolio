package com.codernawaki.portfolio;

import java.time.Year;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    private final PortfolioService portfolioService;

    public GlobalModelAttributeAdvice(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        model.addAttribute("displayName", props.getDisplayName());
        model.addAttribute("githubUrl", props.getGithubUrl());
        model.addAttribute("email", props.getEmail());
        model.addAttribute("currentYear", Year.now().getValue());
    }
}
