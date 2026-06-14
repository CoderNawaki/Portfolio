package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

class BlogServiceTest {

    private ArticleRepository articleRepository;
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        blogService = new BlogService(articleRepository);
    }

    @Test
    void renderHtmlConvertsBoldMarkdown() {
        String result = blogService.renderHtml("**bold**");
        assertTrue(result.contains("<strong>bold</strong>") || result.contains("<strong>bold</strong>"));
    }

    @Test
    void renderHtmlReturnsEmptyForNull() {
        assertEquals("", blogService.renderHtml(null));
    }

    @Test
    void renderHtmlReturnsEmptyForBlank() {
        assertEquals("", blogService.renderHtml("   "));
    }

    @Test
    void generateSlugConvertsTitle() {
        assertEquals("hello-world", BlogService.generateSlug("Hello World"));
    }

    @Test
    void generateSlugHandlesSpecialCharacters() {
        assertEquals("java-spring-boot", BlogService.generateSlug("Java & Spring Boot!"));
    }

    @Test
    void generateSlugReturnsUntitledForBlank() {
        assertEquals("untitled", BlogService.generateSlug("   "));
    }

    @Test
    void generateSlugReturnsUntitledForNull() {
        assertEquals("untitled", BlogService.generateSlug(null));
    }

    @Test
    void getPublishedArticlesDelegatesToRepository() {
        Page<Article> expected = new PageImpl<>(List.of());
        when(articleRepository.findByStatus(eq(ArticleStatus.PUBLISHED), any(Pageable.class))).thenReturn(expected);

        Page<Article> result = blogService.getPublishedArticles(PageRequest.of(0, 10));

        assertSame(expected, result);
    }

    @Test
    void getPublishedArticlesWithTagDelegatesToRepository() {
        Page<Article> expected = new PageImpl<>(List.of());
        when(articleRepository.findByStatusAndTag(eq(ArticleStatus.PUBLISHED), anyString(), any(Pageable.class)))
                .thenReturn(expected);

        Page<Article> result = blogService.getPublishedArticles(PageRequest.of(0, 10), "java");

        assertSame(expected, result);
    }

    @Test
    void getLatestPublishedArticlesReturnsLimitedResults() {
        Article article = new Article();
        article.setId(1L);
        Page<Article> page = new PageImpl<>(List.of(article));
        when(articleRepository.findByStatus(eq(ArticleStatus.PUBLISHED), any(Pageable.class))).thenReturn(page);

        List<Article> result = blogService.getLatestPublishedArticles(3);

        assertEquals(1, result.size());
    }

    @Test
    void getAllPublishedArticlesReturnsArticles() {
        Article article = new Article();
        article.setId(1L);
        Page<Article> page = new PageImpl<>(List.of(article));
        when(articleRepository.findByStatus(eq(ArticleStatus.PUBLISHED), any(Pageable.class))).thenReturn(page);

        List<Article> result = blogService.getAllPublishedArticles();

        assertEquals(1, result.size());
    }

    @Test
    void findBySlugDelegatesToRepository() {
        Article article = new Article();
        article.setSlug("test");
        when(articleRepository.findBySlug("test")).thenReturn(Optional.of(article));

        Optional<Article> result = blogService.findBySlug("test");

        assertTrue(result.isPresent());
        assertEquals("test", result.get().getSlug());
    }

    @Test
    void getAllArticlesDelegatesToRepository() {
        Page<Article> expected = new PageImpl<>(List.of());
        when(articleRepository.findAll(any(Pageable.class))).thenReturn(expected);

        Page<Article> result = blogService.getAllArticles(PageRequest.of(0, 10));

        assertSame(expected, result);
    }

    @Test
    void createArticleSavesAndReturnsArticle() {
        when(articleRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        Article saved = new Article();
        saved.setId(1L);
        saved.setTitle("Test");
        when(articleRepository.save(any())).thenReturn(saved);

        ArticleForm form = new ArticleForm();
        form.setTitle("Test");
        form.setContent("Content");

        Article result = blogService.createArticle(form);

        assertEquals("Test", result.getTitle());
        verify(articleRepository).save(any());
    }

    @Test
    void updateArticleUpdatesExistingArticle() {
        Article existing = new Article();
        existing.setId(1L);
        existing.setTitle("Old Title");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(articleRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        when(articleRepository.save(any())).thenReturn(existing);

        ArticleForm form = new ArticleForm();
        form.setTitle("New Title");
        form.setContent("New Content");

        Article result = blogService.updateArticle(1L, form);

        assertEquals("New Title", result.getTitle());
    }

    @Test
    void updateArticleThrowsOnNotFound() {
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        ArticleForm form = new ArticleForm();
        form.setTitle("Title");
        form.setContent("Content");

        assertThrows(ResponseStatusException.class, () -> blogService.updateArticle(99L, form));
    }

    @Test
    void deleteArticleDeletesExistingArticle() {
        Article existing = new Article();
        existing.setId(1L);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(existing));

        blogService.deleteArticle(1L);

        verify(articleRepository).delete(existing);
    }

    @Test
    void deleteArticleThrowsOnNotFound() {
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> blogService.deleteArticle(99L));
    }

    @Test
    void createArticlePublishesWhenFormHasPublishFlag() {
        when(articleRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        Article saved = new Article();
        saved.setId(1L);
        saved.setTitle("Test");
        saved.setStatus(ArticleStatus.PUBLISHED);
        when(articleRepository.save(any())).thenReturn(saved);

        ArticleForm form = new ArticleForm();
        form.setTitle("Test");
        form.setContent("Content");
        form.setPublish(true);

        Article result = blogService.createArticle(form);

        assertEquals(ArticleStatus.PUBLISHED, result.getStatus());
    }
}
