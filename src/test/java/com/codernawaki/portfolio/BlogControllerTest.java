package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class BlogControllerTest {

    private MockMvc mockMvc;
    private BlogService blogService;
    private PortfolioService portfolioService;
    private PortfolioProperties properties;
    private GithubService githubService;

    @BeforeEach
    void setUp() {
        properties = new PortfolioProperties();
        properties.setDisplayName("Lama Nawaraj");
        properties.setSiteUrl("http://localhost:8081");
        properties.setGithubUrl("https://github.com/CoderNawaki");
        properties.setEmail("lama@example.com");

        githubService = mock(GithubService.class);
        portfolioService = new PortfolioService(properties, githubService);
        blogService = mock(BlogService.class);

        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setSlug("test-article");
        article.setContent("**bold** content");
        article.setExcerpt("A test excerpt");
        article.setTags("java, spring");
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(Instant.now());

        Page<Article> articlePage = new PageImpl<>(List.of(article), PageRequest.of(0, 10), 1);
        when(blogService.getPublishedArticles(any(PageRequest.class), isNull())).thenReturn(articlePage);
        when(blogService.getPublishedArticles(any(PageRequest.class), anyString())).thenReturn(articlePage);
        when(blogService.findBySlug("test-article")).thenReturn(Optional.of(article));
        when(blogService.findBySlug("unknown")).thenReturn(Optional.empty());
        when(blogService.renderHtml("**bold** content")).thenReturn("<strong>bold</strong> content");

        mockMvc = MockMvcBuilders.standaloneSetup(new BlogController(blogService, portfolioService))
                .setViewResolvers(thymeleafViewResolver())
                .build();
    }

    @Test
    void shouldRenderBlogListPage() throws Exception {
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/list"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Article")));
    }

    @Test
    void shouldRenderBlogDetailPage() throws Exception {
        mockMvc.perform(get("/blog/test-article"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("htmlContent"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Article")));
    }

    @Test
    void shouldReturn404ForUnknownSlug() throws Exception {
        mockMvc.perform(get("/blog/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRenderRssFeed() throws Exception {
        when(blogService.getAllPublishedArticles()).thenReturn(List.of());

        mockMvc.perform(get("/blog/feed.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<rss")));
    }

    @Test
    void shouldFilterByTag() throws Exception {
        mockMvc.perform(get("/blog").param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/list"))
                .andExpect(model().attribute("currentTag", "java"));
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
