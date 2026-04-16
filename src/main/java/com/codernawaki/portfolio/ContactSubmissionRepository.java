package com.codernawaki.portfolio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {

    @Query("""
            select submission
            from ContactSubmission submission
            where (:status is null or submission.status = :status)
              and (:query is null
                   or lower(submission.name) like lower(concat('%', :query, '%'))
                   or lower(submission.email) like lower(concat('%', :query, '%'))
                   or lower(submission.message) like lower(concat('%', :query, '%'))
                   or lower(coalesce(submission.adminNote, '')) like lower(concat('%', :query, '%')))
            order by submission.createdAt desc
            """)
    Page<ContactSubmission> search(
            @Param("query") String query,
            @Param("status") ContactSubmissionStatus status,
            Pageable pageable);
}
