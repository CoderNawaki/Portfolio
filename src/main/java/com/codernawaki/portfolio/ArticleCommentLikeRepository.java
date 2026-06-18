package com.codernawaki.portfolio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentLikeRepository extends JpaRepository<ArticleCommentLike, Long> {

    int countByCommentId(Long commentId);

    Optional<ArticleCommentLike> findByCommentIdAndIpAddress(Long commentId, String ipAddress);

    void deleteByCommentIdAndIpAddress(Long commentId, String ipAddress);

    void deleteAllByCommentIdIn(java.util.Collection<Long> commentIds);
}
