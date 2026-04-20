package com.codernawaki.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioServiceTest {

    private PortfolioProperties properties;
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        properties = new PortfolioProperties();
        properties.setDisplayName("Lama");
        properties.setRole("Developer");
        
        PortfolioProperties.FeaturedProjectProperties project = new PortfolioProperties.FeaturedProjectProperties();
        project.setSlug("project-1");
        project.setTitle("Project 1");
        project.setStack("Java");
        properties.setProjects(List.of(project));
        
        portfolioService = new PortfolioService(properties);
    }

    @Test
    void shouldReturnProperties() {
        assertThat(portfolioService.getProperties().getDisplayName()).isEqualTo("Lama");
    }

    @Test
    void shouldMapPropertiesToFeaturedProjects() {
        List<FeaturedProject> projects = portfolioService.getFeaturedProjects();
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).slug()).isEqualTo("project-1");
        assertThat(projects.get(0).title()).isEqualTo("Project 1");
        assertThat(projects.get(0).stack()).isEqualTo("Java");
    }
}
