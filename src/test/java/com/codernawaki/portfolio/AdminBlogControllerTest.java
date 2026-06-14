package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class AdminBlogControllerTest {

    private MockMvc mockMvc;
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        blogService = mock(BlogService.class);

        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setSlug("test-article");
        article.setContent("Content");
        article.setStatus(ArticleStatus.DRAFT);
        article.setCreatedAt(Instant.now());
        article.setUpdatedAt(Instant.now());

        Page<Article> articlePage = new PageImpl<>(List.of(article), PageRequest.of(0, 10), 1);
        when(blogService.getAllArticles(any(PageRequest.class))).thenReturn(articlePage);
        when(blogService.findById(1L)).thenReturn(Optional.of(article));

        mockMvc = MockMvcBuilders.standaloneSetup(new AdminBlogController(blogService))
                .setViewResolvers(thymeleafViewResolver())
                .build();
    }

    @Test
    void shouldRenderArticlesPage() throws Exception {
        mockMvc.perform(get("/admin/articles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/articles"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("articlesPage"));
    }

    @Test
    void shouldRenderArticlesPageWithSelectedArticle() throws Exception {
        mockMvc.perform(get("/admin/articles").param("selected", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/articles"))
                .andExpect(model().attributeExists("selectedArticle"));
    }

    @Test
    void shouldRedirectAfterCreate() throws Exception {
        mockMvc.perform(post("/admin/articles")
                        .param("title", "New Article")
                        .param("content", "Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/articles"));

        verify(blogService).createArticle(any());
    }

    @Test
    void shouldRedirectAfterUpdate() throws Exception {
        mockMvc.perform(post("/admin/articles/1")
                        .param("title", "Updated")
                        .param("content", "Updated Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/articles?selected=1"));

        verify(blogService).updateArticle(eq(1L), any());
    }

    @Test
    void shouldRedirectAfterDelete() throws Exception {
        mockMvc.perform(post("/admin/articles/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/articles"));

        verify(blogService).deleteArticle(1L);
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
