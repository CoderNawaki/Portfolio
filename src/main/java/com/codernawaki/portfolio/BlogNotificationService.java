package com.codernawaki.portfolio;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BlogNotificationService {

    private static final Sort NOTIFICATION_SORT = Sort.by(Sort.Direction.DESC, "publishedAt");

    private final BlogNotificationRepository notificationRepository;
    private final ArticleRepository articleRepository;

    public BlogNotificationService(BlogNotificationRepository notificationRepository,
                                   ArticleRepository articleRepository) {
        this.notificationRepository = notificationRepository;
        this.articleRepository = articleRepository;
    }

    public void recordPublication(Article article) {
        if (article == null || article.getId() == null || article.getPublishedAt() == null) {
            return;
        }
        if (notificationRepository.existsByArticleId(article.getId())) {
            return;
        }

        BlogNotification notification = new BlogNotification();
        notification.setArticleId(article.getId());
        notification.setArticleTitle(article.getTitle());
        notification.setArticleSlug(article.getSlug());
        notification.setMessage("A new article is live on the blog.");
        notification.setPublishedAt(article.getPublishedAt());
        notificationRepository.save(notification);
    }

    public Page<BlogNotificationView> getNotifications(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                NOTIFICATION_SORT);
        return notificationRepository.findAll(sorted).map(this::toView);
    }

    public List<BlogNotificationView> getLatestNotifications(int count) {
        Pageable limit = PageRequest.of(0, count, NOTIFICATION_SORT);
        return notificationRepository.findAll(limit).map(this::toView).getContent();
    }

    private BlogNotificationView toView(BlogNotification notification) {
        String currentSlug = articleRepository.findById(notification.getArticleId())
                .map(Article::getSlug)
                .orElse(notification.getArticleSlug());
        return new BlogNotificationView(
                notification.getArticleId(),
                notification.getArticleTitle(),
                currentSlug,
                notification.getMessage(),
                notification.getPublishedAt());
    }
}
