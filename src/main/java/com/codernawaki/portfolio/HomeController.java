package com.codernawaki.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
        model.addAttribute("pageTitle", props.getDisplayName() + " | Full Stack Developer Portfolio");
        model.addAttribute("pageDescription",
                "Portfolio of Lama Nawaraj, a Java-first full stack developer in Japan covering Spring Boot, frontend delivery, testing, and recruiter-ready project case studies.");
        model.addAttribute("pageUrl", props.getSiteUrl() + "/");
        return "index";
    }

    @GetMapping("/projects/{slug}")
    public String projectDetail(@PathVariable String slug, Model model) {
        FeaturedProject project = portfolioService.findProjectBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found."));
        PortfolioProperties props = portfolioService.getProperties();

        model.addAttribute("project", project);
        model.addAttribute("pageTitle", project.title() + " | Case Study | " + props.getDisplayName());
        model.addAttribute("pageDescription", project.tagline() + " Built with " + project.stack() + ".");
        model.addAttribute("pageUrl", props.getSiteUrl() + "/projects/" + project.slug());
        return "project-detail";
    }

    @GetMapping("/login")
    public String login(Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        model.addAttribute("pageTitle", "Admin Login | " + props.getDisplayName());
        model.addAttribute("pageDescription", "Secure admin login for reviewing portfolio contact submissions.");
        model.addAttribute("pageUrl", props.getSiteUrl() + "/login");
        return "login";
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String sitemap() {
        PortfolioProperties props = portfolioService.getProperties();
        String baseUrl = props.getSiteUrl();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        appendSitemapUrl(xml, baseUrl + "/");
        appendSitemapUrl(xml, baseUrl + "/login");
        for (FeaturedProject project : portfolioService.getFeaturedProjects()) {
            appendSitemapUrl(xml, baseUrl + "/projects/" + project.slug());
        }
        xml.append("</urlset>");
        return xml.toString();
    }

    private void appendSitemapUrl(StringBuilder xml, String url) {
        xml.append("<url><loc>")
                .append(url)
                .append("</loc></url>");
    }
}
