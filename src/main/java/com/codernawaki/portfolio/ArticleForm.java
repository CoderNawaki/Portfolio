package com.codernawaki.portfolio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArticleForm {

    @NotBlank(message = "Title is required.")
    @Size(max = 200, message = "Title must be 200 characters or fewer.")
    private String title;

    @NotBlank(message = "Content is required.")
    @Size(max = 100000, message = "Content must be 100000 characters or fewer.")
    private String content;

    @Size(max = 500, message = "Excerpt must be 500 characters or fewer.")
    private String excerpt;

    @Size(max = 500, message = "Tags must be 500 characters or fewer.")
    private String tags;

    private boolean publish;

    private String slug;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getTags() {
        return tags;
    }

    public boolean isPublish() {
        return publish;
    }

    public String getSlug() {
        return slug;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
