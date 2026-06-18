package com.codernawaki.portfolio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article_comments")
public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = true)
    private Long parentId;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Transient
    private List<ArticleComment> replies = new ArrayList<>();

    @Transient
    private int likeCount;

    @Transient
    private boolean likedByCurrentUser;

    public Long getId() { return id; }
    public Long getArticleId() { return articleId; }
    public Long getParentId() { return parentId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ArticleComment> getReplies() { return replies; }
    public int getLikeCount() { return likeCount; }
    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }

    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public void setAuthor(String author) { this.author = author; }
    public void setContent(String content) { this.content = content; }
    public void setReplies(List<ArticleComment> replies) { this.replies = replies; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
