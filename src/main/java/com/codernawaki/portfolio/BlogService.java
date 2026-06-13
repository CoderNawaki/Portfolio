package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

@Service
public class BlogService {

    private static final Sort PUBLISHED_SORT = Sort.by(Sort.Direction.DESC, "publishedAt");
    private static final Sort ADMIN_SORT = Sort.by(Sort.Direction.DESC, "updatedAt");

    private final ArticleRepository articleRepository;
    private final Parser markdownParser;
    private final HtmlRenderer markdownRenderer;

    public BlogService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        MutableDataSet options = new MutableDataSet();
        this.markdownParser = Parser.builder(options).build();
        this.markdownRenderer = HtmlRenderer.builder(options).build();
    }

    public Page<Article> getPublishedArticles(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                PUBLISHED_SORT);
        return articleRepository.findByStatus(ArticleStatus.PUBLISHED, sorted);
    }

    public List<Article> getLatestPublishedArticles(int count) {
        Pageable limit = PageRequest.of(0, count, PUBLISHED_SORT);
        return articleRepository.findByStatus(ArticleStatus.PUBLISHED, limit).getContent();
    }

    public Page<Article> getAllArticles(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                ADMIN_SORT);
        return articleRepository.findAll(sorted);
    }

    public Optional<Article> findBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    public Article createArticle(ArticleForm form) {
        Article article = new Article();
        applyForm(article, form);
        article.setSlug(resolveSlug(form, null));
        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, ArticleForm form) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));
        applyForm(article, form);
        article.setSlug(resolveSlug(form, id));
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));
        articleRepository.delete(article);
    }

    public String renderHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        return markdownRenderer.render(markdownParser.parse(markdown));
    }

    private void applyForm(Article article, ArticleForm form) {
        article.setTitle(form.getTitle());
        article.setContent(form.getContent());

        if (form.getExcerpt() != null && !form.getExcerpt().isBlank()) {
            article.setExcerpt(form.getExcerpt().trim());
        } else {
            article.setExcerpt(null);
        }

        if (form.getTags() != null && !form.getTags().isBlank()) {
            article.setTags(form.getTags().trim());
        } else {
            article.setTags(null);
        }

        if (form.isPublish() && article.getStatus() != ArticleStatus.PUBLISHED) {
            article.setStatus(ArticleStatus.PUBLISHED);
            article.setPublishedAt(Instant.now());
        } else if (!form.isPublish()) {
            article.setStatus(ArticleStatus.DRAFT);
            article.setPublishedAt(null);
        }
    }

    private String resolveSlug(ArticleForm form, Long existingId) {
        String base = form.getSlug();
        if (base == null || base.isBlank()) {
            base = generateSlug(form.getTitle());
        } else {
            base = generateSlug(base);
        }

        String slug = base;
        int counter = 1;
        while (isSlugTaken(slug, existingId)) {
            slug = base + "-" + counter;
            counter++;
        }
        return slug;
    }

    private boolean isSlugTaken(String slug, Long excludeId) {
        return articleRepository.findBySlug(slug)
                .map(a -> !a.getId().equals(excludeId))
                .orElse(false);
    }

    static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "untitled";
        }
        String slug = input.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return slug.isBlank() ? "untitled" : slug;
    }
}
