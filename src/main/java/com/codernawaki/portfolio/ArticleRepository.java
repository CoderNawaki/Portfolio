package com.codernawaki.portfolio;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findBySlug(String slug);

    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);

    @Query("""
            select a from Article a
            where a.status = :status
              and (:tag is null or lower(a.tags) like lower(cast(:tag as string)))
            """)
    Page<Article> findByStatusAndTag(@Param("status") ArticleStatus status,
                                     @Param("tag") String tag,
                                     Pageable pageable);

    boolean existsBySlug(String slug);
}
