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

    public BlogNotificationService(BlogNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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

    public Page<BlogNotification> getNotifications(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                NOTIFICATION_SORT);
        return notificationRepository.findAll(sorted);
    }

    public List<BlogNotification> getLatestNotifications(int count) {
        Pageable limit = PageRequest.of(0, count, NOTIFICATION_SORT);
        return notificationRepository.findAll(limit).getContent();
    }
}
