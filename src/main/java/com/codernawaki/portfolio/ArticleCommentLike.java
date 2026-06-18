package com.codernawaki.portfolio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "article_comment_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"comment_id", "ip_address"})
})
public class ArticleCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public Long getCommentId() { return commentId; }
    public String getIpAddress() { return ipAddress; }
    public Instant getCreatedAt() { return createdAt; }

    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
