package com.codernawaki.portfolio;

import java.io.Serializable;

public record FeaturedProject(
        String slug,
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
        String accessNote) implements Serializable {
    private static final long serialVersionUID = 1L;
}
