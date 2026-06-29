package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class BlogNotificationServiceTest {

    private BlogNotificationRepository notificationRepository;
    private ArticleRepository articleRepository;
    private PortfolioProperties portfolioProperties;
    private BlogNotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = org.mockito.Mockito.mock(BlogNotificationRepository.class);
        articleRepository = org.mockito.Mockito.mock(ArticleRepository.class);
        portfolioProperties = new PortfolioProperties();
        portfolioProperties.setBlogPublicationNotificationMessage("Fresh article published.");
        notificationService = new BlogNotificationService(notificationRepository, articleRepository, portfolioProperties);
    }

    @Test
    void recordPublicationSavesConfiguredMessage() {
        Article article = publishedArticle();

        notificationService.recordPublication(article);

        ArgumentCaptor<BlogNotification> captor = ArgumentCaptor.forClass(BlogNotification.class);
        verify(notificationRepository).saveAndFlush(captor.capture());
        BlogNotification notification = captor.getValue();
        assertEquals(1L, notification.getArticleId());
        assertEquals("Test Article", notification.getArticleTitle());
        assertEquals("test-article", notification.getArticleSlug());
        assertEquals("Fresh article published.", notification.getMessage());
        verify(notificationRepository, never()).existsByArticleId(anyLong());
    }

    @Test
    void recordPublicationIgnoresDuplicatePublicationRace() {
        Article article = publishedArticle();
        when(notificationRepository.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException("duplicate article_id"));
        when(notificationRepository.existsByArticleId(1L)).thenReturn(true);

        assertDoesNotThrow(() -> notificationService.recordPublication(article));
    }

    @Test
    void recordPublicationRethrowsUnexpectedIntegrityViolation() {
        Article article = publishedArticle();
        when(notificationRepository.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException("not duplicate"));
        when(notificationRepository.existsByArticleId(1L)).thenReturn(false);

        assertThrows(DataIntegrityViolationException.class, () -> notificationService.recordPublication(article));
    }

    @Test
    void getNotificationsMapsStoredFieldsToViews() {
        BlogNotification notification = notification();
        Page<BlogNotification> page = new PageImpl<>(List.of(notification), PageRequest.of(0, 10), 1);
        when(notificationRepository.findAll(any(PageRequest.class))).thenReturn(page);
        Article article = new Article();
        article.setId(10L);
        article.setSlug("updated-article");
        when(articleRepository.findAllById(anyIterable())).thenReturn(List.of(article));

        Page<BlogNotificationView> result = notificationService.getNotifications(PageRequest.of(0, 10));

        BlogNotificationView view = result.getContent().get(0);
        assertEquals(1L, view.id());
        assertEquals(10L, view.articleId());
        assertEquals("New Article", view.articleTitle());
        assertEquals("updated-article", view.articleSlug());
        assertEquals("A new article is live on the blog.", view.message());
        verify(articleRepository).findAllById(anyIterable());
    }

    private Article publishedArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setSlug("test-article");
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(Instant.parse("2024-01-01T00:00:00Z"));
        return article;
    }

    private BlogNotification notification() {
        BlogNotification notification = new BlogNotification();
        notification.setId(1L);
        notification.setArticleId(10L);
        notification.setArticleTitle("New Article");
        notification.setArticleSlug("new-article");
        notification.setMessage("A new article is live on the blog.");
        notification.setPublishedAt(Instant.parse("2024-02-01T00:00:00Z"));
        return notification;
    }
}
