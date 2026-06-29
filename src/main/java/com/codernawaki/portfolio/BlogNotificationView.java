package com.codernawaki.portfolio;

import java.time.Instant;

public record BlogNotificationView(
        Long id,
        Long articleId,
        String articleTitle,
        String articleSlug,
        String message,
        Instant publishedAt) {

    public static BlogNotificationView from(BlogNotification notification) {
        return new BlogNotificationView(
                notification.getId(),
                notification.getArticleId(),
                notification.getArticleTitle(),
                notification.getArticleSlug(),
                notification.getMessage(),
                notification.getPublishedAt());
    }
}
