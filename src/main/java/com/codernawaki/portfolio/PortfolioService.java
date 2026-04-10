package com.codernawaki.portfolio;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    private final PortfolioProperties properties;

    public PortfolioService(PortfolioProperties properties) {
        this.properties = properties;
    }

    public PortfolioProperties getProperties() {
        return properties;
    }

    public List<FeaturedProject> getFeaturedProjects() {
        return properties.getProjects().stream()
                .map(p -> new FeaturedProject(
                        p.getTitle(),
                        p.getTagline(),
                        p.getStack(),
                        p.getProblem(),
                        p.getBackendFocus(),
                        p.getFrontendFocus(),
                        p.getOutcome(),
                        p.getHighlight(),
                        p.getGithubUrl(),
                        p.getVisibility(),
                        p.getAccessNote()
                ))
                .collect(Collectors.toList());
    }
}
