package com.codernawaki.portfolio;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortfolioServiceTest {

    private PortfolioProperties properties;
    private GithubService githubService;
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
        project.setVisibility("Public");
        project.setGithubUrl("https://github.com/owner/repo");
        properties.setProjects(List.of(project));
        
        githubService = mock(GithubService.class);
        when(githubService.getRepositoryStats(anyString()))
                .thenReturn(new GithubStats(10, "2024-01-01T00:00:00Z", "repo"));
        
        portfolioService = new PortfolioService(properties, githubService);
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
