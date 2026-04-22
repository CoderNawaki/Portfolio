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
              and (cast(:query as string) is null
                   or lower(submission.name) like cast(:query as string)
                   or lower(submission.email) like cast(:query as string)
                   or lower(submission.message) like cast(:query as string)
                   or (submission.adminNote is not null
                       and lower(submission.adminNote) like cast(:query as string)))
            """)
    Page<ContactSubmission> search(
            @Param("query") String query,
            @Param("status") ContactSubmissionStatus status,
            Pageable pageable);
}
