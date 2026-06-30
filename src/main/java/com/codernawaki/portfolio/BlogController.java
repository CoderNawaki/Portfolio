package com.codernawaki.portfolio;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class BlogController {

    private static final int ARTICLES_PER_PAGE = 10;

    private final BlogService blogService;
    private final PortfolioService portfolioService;
    private final ArticleEngagementService engagementService;

    public BlogController(BlogService blogService, PortfolioService portfolioService,
                          ArticleEngagementService engagementService) {
        this.blogService = blogService;
        this.portfolioService = portfolioService;
        this.engagementService = engagementService;
    }

    @GetMapping("/blog")
    public String blogList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String tag,
            Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        var articlesPage = blogService.getPublishedArticles(PageRequest.of(page, ARTICLES_PER_PAGE), tag);

        Map<Long, Integer> readingTimes = new HashMap<>();
        for (Article article : articlesPage.getContent()) {
            readingTimes.put(article.getId(), blogService.estimateReadingTime(article.getContent()));
        }

        model.addAttribute("articlesPage", articlesPage);
        model.addAttribute("articles", articlesPage.getContent());
        model.addAttribute("readingTimes", readingTimes);
        model.addAttribute("currentTag", tag);
        model.addAttribute("pageTitle", tag != null ? tag + " | Blog | " + props.getDisplayName() : "Blog | " + props.getDisplayName());
        model.addAttribute("pageDescription", "Articles and case studies from a full stack developer in Japan.");
        model.addAttribute("pageUrl", props.getSiteUrl() + "/blog");
        return "blog/list";
    }

    @GetMapping("/blog/{slug}")
    public String blogDetail(@PathVariable String slug, Model model, HttpServletRequest request) {
        PortfolioProperties props = portfolioService.getProperties();
        Article article = blogService.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));

        String ip = getClientIp(request);
        int likeCount = engagementService.getArticleLikeCount(article.getId());
        boolean liked = engagementService.hasLikedArticle(article.getId(), ip);
        List<ArticleComment> comments = engagementService.getCommentTree(article.getId(), ip);

        model.addAttribute("article", article);
        model.addAttribute("htmlContent", blogService.renderHtml(article.getContent()));
        model.addAttribute("readingTime", blogService.estimateReadingTime(article.getContent()));
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("liked", liked);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new CommentForm());
        model.addAttribute("pageTitle", article.getTitle() + " | Blog | " + props.getDisplayName());
        model.addAttribute("pageDescription",
                article.getExcerpt() != null ? article.getExcerpt() : article.getTitle());
        model.addAttribute("pageUrl", props.getSiteUrl() + "/blog/" + article.getSlug());
        return "blog/detail";
    }

    @PostMapping("/blog/{slug}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable String slug, HttpServletRequest request) {
        Article article = blogService.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));
        Map<String, Object> result = engagementService.toggleArticleLike(article.getId(), getClientIp(request));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/blog/{slug}/comment")
    @ResponseBody
    public ResponseEntity<?> addComment(@PathVariable String slug,
                                        @Valid @RequestBody CommentForm form,
                                        HttpServletRequest request) {
        Article article = blogService.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));
        ArticleComment comment = engagementService.addComment(article.getId(), form.getParentId(), form, getClientIp(request));
        return ResponseEntity.ok(Map.of("id", comment.getId(), "message", "Comment added."));
    }

    @GetMapping("/blog/{slug}/comments")
    @ResponseBody
    public ResponseEntity<List<ArticleComment>> getComments(@PathVariable String slug, HttpServletRequest request) {
        Article article = blogService.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));
        List<ArticleComment> comments = engagementService.getCommentTree(article.getId(), getClientIp(request));
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/blog/comments/{commentId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleCommentLike(@PathVariable Long commentId,
                                                                  HttpServletRequest request) {
        Map<String, Object> result = engagementService.toggleCommentLike(commentId, getClientIp(request));
        return ResponseEntity.ok(result);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String cfConnectingIp = request.getHeader("CF-Connecting-IP");
        if (cfConnectingIp != null && !cfConnectingIp.isBlank()) {
            return cfConnectingIp;
        }
        return request.getRemoteAddr();
    }

    @GetMapping(value = "/blog/feed.xml", produces = "application/xml")
    @ResponseBody
    public String rssFeed() {
        PortfolioProperties props = portfolioService.getProperties();
        String baseUrl = props.getSiteUrl();
        List<Article> articles = blogService.getAllPublishedArticles();
        DateTimeFormatter dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME;

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
        xml.append("<channel>");
        xml.append("<title>").append(xmlEscape(props.getDisplayName())).append(" Blog</title>");
        xml.append("<link>").append(xmlEscape(baseUrl)).append("/blog</link>");
        xml.append("<description>Articles and case studies from a full stack developer in Japan.</description>");
        xml.append("<atom:link href=\"").append(xmlEscape(baseUrl)).append("/blog/feed.xml\" rel=\"self\" type=\"application/rss+xml\"/>");

        for (Article article : articles) {
            xml.append("<item>");
            xml.append("<title>").append(xmlEscape(article.getTitle())).append("</title>");
            xml.append("<link>").append(xmlEscape(baseUrl)).append("/blog/").append(xmlEscape(article.getSlug())).append("</link>");
            xml.append("<guid>").append(xmlEscape(baseUrl)).append("/blog/").append(xmlEscape(article.getSlug())).append("</guid>");
            if (article.getExcerpt() != null) {
                xml.append("<description>").append(xmlEscape(article.getExcerpt())).append("</description>");
            }
            if (article.getPublishedAt() != null) {
                xml.append("<pubDate>").append(dateFormat.format(article.getPublishedAt())).append("</pubDate>");
            }
            xml.append("</item>");
        }

        xml.append("</channel>");
        xml.append("</rss>");
        return xml.toString();
    }

    private String xmlEscape(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
