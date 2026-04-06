package com.codernawaki.portfolio;

public record FeaturedProject(
        String title,
        String tagline,
        String stack,
        String problem,
        String backendFocus,
        String frontendFocus,
        String outcome,
        String highlight,
        String githubUrl,
        String visibility,
        String accessNote) {
}
