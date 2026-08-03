package com.codernawaki.portfolio;

import java.time.Instant;

public record BlogNotificationView(
        Long id,
        Long articleId,
        String articleTitle,
        String articleSlug,
        String message,
        Instant publishedAt) {
}
