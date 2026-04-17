package com.codernawaki.portfolio;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "portfolio")
public class PortfolioProperties {

    private String displayName;
    private String role;
    private String location;
    private String githubUrl;
    private String email;
    private String resumeLabel;
    private String availability;
    private List<String> focusAreas;
    private List<String> proofPoints;
    private String bio;
    private List<FeaturedProjectProperties> projects;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResumeLabel() { return resumeLabel; }
    public void setResumeLabel(String resumeLabel) { this.resumeLabel = resumeLabel; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public List<String> getFocusAreas() { return focusAreas; }
    public void setFocusAreas(List<String> focusAreas) { this.focusAreas = focusAreas; }

    public List<String> getProofPoints() { return proofPoints; }
    public void setProofPoints(List<String> proofPoints) { this.proofPoints = proofPoints; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<FeaturedProjectProperties> getProjects() { return projects; }
    public void setProjects(List<FeaturedProjectProperties> projects) { this.projects = projects; }

    public static class FeaturedProjectProperties {
        private String slug;
        private String title;
        private String tagline;
        private String stack;
        private String problem;
        private String backendFocus;
        private String frontendFocus;
        private String outcome;
        private String highlight;
        private String githubUrl;
        private String visibility;
        private String accessNote;

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getTagline() { return tagline; }
        public void setTagline(String tagline) { this.tagline = tagline; }

        public String getStack() { return stack; }
        public void setStack(String stack) { this.stack = stack; }

        public String getProblem() { return problem; }
        public void setProblem(String problem) { this.problem = problem; }

        public String getBackendFocus() { return backendFocus; }
        public void setBackendFocus(String backendFocus) { this.backendFocus = backendFocus; }

        public String getFrontendFocus() { return frontendFocus; }
        public void setFrontendFocus(String frontendFocus) { this.frontendFocus = frontendFocus; }

        public String getOutcome() { return outcome; }
        public void setOutcome(String outcome) { this.outcome = outcome; }

        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }

        public String getGithubUrl() { return githubUrl; }
        public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

        public String getVisibility() { return visibility; }
        public void setVisibility(String visibility) { this.visibility = visibility; }

        public String getAccessNote() { return accessNote; }
        public void setAccessNote(String accessNote) { this.accessNote = accessNote; }
    }
}
