package com.codernawaki.portfolio;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class HomeControllerTest {

    private MockMvc mockMvc;
    private PortfolioService portfolioService;
    private PortfolioProperties properties;

    @BeforeEach
    void setUp() {
        properties = new PortfolioProperties();
        properties.setDisplayName("Lama Nawaraj");
        properties.setRole("Full Stack Developer");
        properties.setLocation("Working mainly in Japan");
        properties.setResumeLabel("Resume available on request");
        properties.setAvailability("Open to full stack opportunities.");
        properties.setBio("Professional Java developer.");
        properties.setFocusAreas(List.of("Java and Spring Boot"));
        properties.setProofPoints(List.of("Working mainly in Japan"));
        properties.setGithubUrl("https://github.com/CoderNawaki");
        properties.setEmail("lama@example.com");
        properties.setSiteUrl("http://localhost:8081");
        PortfolioProperties.FeaturedProjectProperties project = new PortfolioProperties.FeaturedProjectProperties();
        project.setSlug("portfolio");
        project.setTitle("Portfolio");
        project.setTagline("Structured Spring Boot portfolio rebuild");
        project.setStack("Java, Spring Boot");
        project.setProblem("Refactoring a basic personal site.");
        project.setBackendFocus("Backend restructuring.");
        project.setFrontendFocus("Frontend improvement.");
        project.setOutcome("Stronger recruiter-facing presentation.");
        project.setHighlight("Template-driven application structure");
        project.setGithubUrl("https://github.com/CoderNawaki/Portfolio");
        project.setVisibility("Public");
        project.setAccessNote("Public repository");
        properties.setProjects(List.of(project));

        portfolioService = new PortfolioService(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(portfolioService))
                .setControllerAdvice(new GlobalModelAttributeAdvice(portfolioService))
                .setViewResolvers(thymeleafViewResolver())
                .build();
    }

    @Test
    void shouldRenderHomePageWithSeoMetadata() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("pageTitle", "Lama Nawaraj | Full Stack Developer Portfolio"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("rel=\"canonical\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("summary_large_image")));
    }

    @Test
    void shouldRenderProjectDetailPage() throws Exception {
        mockMvc.perform(get("/projects/portfolio"))
                .andExpect(status().isOk())
                .andExpect(view().name("project-detail"))
                .andExpect(model().attributeExists("project"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Case Study")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Structured Spring Boot portfolio rebuild")));
    }

    @Test
    void shouldRenderSitemapFromConfiguredSiteUrl() throws Exception {
        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<loc>http://localhost:8081/</loc>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<loc>http://localhost:8081/login</loc>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<loc>http://localhost:8081/projects/portfolio</loc>")));
    }

    private ViewResolver thymeleafViewResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(1);
        return viewResolver;
    }
}
