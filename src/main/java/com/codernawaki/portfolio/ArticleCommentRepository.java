package com.codernawaki.portfolio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    List<ArticleComment> findByArticleIdOrderByCreatedAtAsc(Long articleId);
}
