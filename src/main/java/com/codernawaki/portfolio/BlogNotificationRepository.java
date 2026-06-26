package com.codernawaki.portfolio;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogNotificationRepository extends JpaRepository<BlogNotification, Long> {

    boolean existsByArticleId(Long articleId);

    Optional<BlogNotification> findByArticleId(Long articleId);

    Page<BlogNotification> findAll(Pageable pageable);
}
