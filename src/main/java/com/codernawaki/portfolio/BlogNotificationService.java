package com.codernawaki.portfolio;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogNotificationService {

    private static final Sort NOTIFICATION_SORT = Sort.by(Sort.Direction.DESC, "publishedAt");
    private static final String DEFAULT_PUBLICATION_MESSAGE = "A new article is live on the blog.";

    private final BlogNotificationRepository notificationRepository;
    private final ArticleRepository articleRepository;
    private final String publicationMessage;

    @Autowired
    public BlogNotificationService(BlogNotificationRepository notificationRepository,
                                   ArticleRepository articleRepository,
                                   PortfolioProperties portfolioProperties) {
        this.notificationRepository = notificationRepository;
        this.articleRepository = articleRepository;
        this.publicationMessage = resolvePublicationMessage(portfolioProperties);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void recordPublication(Article article) {
        if (article == null || article.getId() == null || article.getPublishedAt() == null) {
            return;
        }

        BlogNotification notification = new BlogNotification();
        notification.setArticleId(article.getId());
        notification.setArticleTitle(article.getTitle());
        notification.setArticleSlug(article.getSlug());
        notification.setMessage(publicationMessage);
        notification.setPublishedAt(article.getPublishedAt());
        try {
            notificationRepository.saveAndFlush(notification);
        } catch (DataIntegrityViolationException ex) {
            if (!notificationRepository.existsByArticleId(article.getId())) {
                throw ex;
            }
        }
    }

    public Page<BlogNotificationView> getNotifications(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                NOTIFICATION_SORT);
        Page<BlogNotification> notifications = notificationRepository.findAll(sorted);
        Map<Long, String> currentSlugs = currentSlugsFor(notifications.getContent());
        return notifications.map(notification -> toView(notification, currentSlugs));
    }

    public List<BlogNotificationView> getLatestNotifications(int count) {
        Pageable limit = PageRequest.of(0, count, NOTIFICATION_SORT);
        return toViews(notificationRepository.findAll(limit).getContent());
    }

    private BlogNotificationView toView(BlogNotification notification, Map<Long, String> currentSlugs) {
        String currentSlug = currentSlugs.getOrDefault(notification.getArticleId(), notification.getArticleSlug());
        return new BlogNotificationView(
                notification.getId(),
                notification.getArticleId(),
                notification.getArticleTitle(),
                currentSlug,
                notification.getMessage(),
                notification.getPublishedAt());
    }

    private List<BlogNotificationView> toViews(List<BlogNotification> notifications) {
        Map<Long, String> currentSlugs = currentSlugsFor(notifications);
        return notifications.stream()
                .map(notification -> toView(notification, currentSlugs))
                .toList();
    }

    private Map<Long, String> currentSlugsFor(List<BlogNotification> notifications) {
        Set<Long> articleIds = notifications.stream()
                .map(BlogNotification::getArticleId)
                .collect(Collectors.toSet());
        if (articleIds.isEmpty()) {
            return Map.of();
        }

        return articleRepository.findAllById(articleIds).stream()
                .collect(Collectors.toMap(Article::getId, Article::getSlug));
    }

    private String resolvePublicationMessage(PortfolioProperties portfolioProperties) {
        String configuredMessage = portfolioProperties.getBlogPublicationNotificationMessage();
        if (configuredMessage == null || configuredMessage.isBlank()) {
            return DEFAULT_PUBLICATION_MESSAGE;
        }
        return configuredMessage.trim();
    }
}
