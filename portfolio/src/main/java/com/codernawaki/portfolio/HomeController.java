package com.codernawaki.portfolio;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("displayName", "Lama Nawaraj");
        model.addAttribute("role", "Full Stack Developer");
        model.addAttribute("location", "Working mainly in Japan");
        model.addAttribute("githubUrl", "https://github.com/CoderNawaki");
        model.addAttribute("email", "lama.nawraj00@gmail.com");
        model.addAttribute("resumeLabel", "Resume available on request");
        model.addAttribute("bio",
                """
                Professional Java developer working mainly in Japan. Experienced in writing design documents,
                development, and testing. Strong language proficiency in Japanese, with English as a secondary
                language. Experienced in bridge engineer work and committed to learning new technology every day.
                """.replace("\n", " "));
        model.addAttribute("projects", featuredProjects());
        return "index";
    }

    private List<FeaturedProject> featuredProjects() {
        return List.of(
                new FeaturedProject(
                        "WebKintaiSystem",
                        "Attendance platform for day-to-day operations, submission control, and business workflow reliability",
                        "Java, Servlet, JSP, Maven, Jetty, H2, SQLite, Oracle",
                        "Built a business-facing attendance system covering clock-in and clock-out, monthly submission, settings, and account-oriented flows.",
                        "Separated authentication, business logic, and persistence through filters, servlets, services, repositories, and database-backed workflows.",
                        "Delivered JSP-based screens designed for recurring internal usage and operational task flow.",
                        "https://github.com/CoderNawaki/WebKintaiSystem",
                        "Private",
                        "Repository is private. Architecture and implementation details can be explained in interview or case-study format."),
                new FeaturedProject(
                        "YouTube Clone",
                        "Frontend product clone focused on search, discovery, testing, and API-driven user experience",
                        "React, JavaScript, RapidAPI, React Testing Library, MSW, Playwright",
                        "Built a YouTube-style browsing experience with category feeds, search results, video detail pages, related content, and channel screens.",
                        "Integrated external API data cleanly while keeping the codebase testable and maintainable.",
                        "Focused on responsive browsing, interaction flow, and modern frontend testing with mocked API coverage.",
                        "https://github.com/CoderNawaki/youtube_clone",
                        "Public",
                        "Public repository with frontend and testing workflow visible."),
                new FeaturedProject(
                        "Portfolio",
                        "Spring Boot portfolio rebuild showing full stack delivery, structure improvement, and recruiter-focused presentation",
                        "Java, Spring Boot, Gradle, HTML, CSS, JavaScript",
                        "Refactoring a basic personal site into a structured application that better reflects full stack capability.",
                        "Using Spring Boot controllers and template rendering to move from static content into maintainable, application-style structure.",
                        "Improving layout, navigation, interaction, and project storytelling for stronger hiring impact.",
                        "https://github.com/CoderNawaki/Portfolio",
                        "Public",
                        "This repository documents the refactor from a simple site into a stronger portfolio application."));
    }
}
