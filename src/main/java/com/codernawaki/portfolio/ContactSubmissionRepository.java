package com.codernawaki.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {

    List<ContactSubmission> findAllByOrderByCreatedAtDesc();
}
