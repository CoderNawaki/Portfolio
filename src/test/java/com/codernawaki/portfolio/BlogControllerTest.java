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
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.test.util.ReflectionTestUtils;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private ArticleEngagementService engagementService;
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
        engagementService = mock(ArticleEngagementService.class);

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
        when(engagementService.getArticleLikeCount(1L)).thenReturn(0);
        when(engagementService.hasLikedArticle(1L, "127.0.0.1")).thenReturn(false);
        when(engagementService.getCommentTree(1L, "127.0.0.1")).thenReturn(List.of());

        mockMvc = MockMvcBuilders.standaloneSetup(new BlogController(blogService, portfolioService, engagementService))
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

    @Test
    void shouldToggleArticleLike() throws Exception {
        when(engagementService.toggleArticleLike(1L, "127.0.0.1"))
                .thenReturn(Map.of("liked", true, "count", 1));

        mockMvc.perform(post("/blog/test-article/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void shouldAddComment() throws Exception {
        ArticleComment savedComment = new ArticleComment();
        ReflectionTestUtils.setField(savedComment, "id", 1L);
        when(engagementService.addComment(eq(1L), isNull(), any(CommentForm.class), anyString()))
                .thenReturn(savedComment);

        mockMvc.perform(post("/blog/test-article/comment")
                        .contentType("application/json")
                        .content("{\"author\":\"Tester\",\"content\":\"Great post!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldGetComments() throws Exception {
        ArticleComment comment = new ArticleComment();
        ReflectionTestUtils.setField(comment, "id", 1L);
        comment.setAuthor("Tester");
        comment.setContent("Nice!");
        when(engagementService.getCommentTree(1L, "127.0.0.1"))
                .thenReturn(List.of(comment));

        mockMvc.perform(get("/blog/test-article/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Tester"));
    }

    @Test
    void shouldToggleCommentLike() throws Exception {
        when(engagementService.toggleCommentLike(5L, "127.0.0.1"))
                .thenReturn(Map.of("liked", false, "count", 3));

        mockMvc.perform(post("/blog/comments/5/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.count").value(3));
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
