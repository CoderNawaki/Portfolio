package com.codernawaki.portfolio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentForm {

    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name must be 100 characters or fewer.")
    private String author;

    @NotBlank(message = "Comment is required.")
    @Size(max = 2000, message = "Comment must be 2000 characters or fewer.")
    private String content;

    private Long parentId;

    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public Long getParentId() { return parentId; }

    public void setAuthor(String author) { this.author = author; }
    public void setContent(String content) { this.content = content; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
