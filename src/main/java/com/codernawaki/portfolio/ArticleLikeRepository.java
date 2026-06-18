package com.codernawaki.portfolio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    int countByArticleId(Long articleId);

    Optional<ArticleLike> findByArticleIdAndIpAddress(Long articleId, String ipAddress);

    void deleteByArticleIdAndIpAddress(Long articleId, String ipAddress);
}
