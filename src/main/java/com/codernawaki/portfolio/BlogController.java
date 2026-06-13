package com.codernawaki.portfolio;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
public class BlogController {

    private static final int ARTICLES_PER_PAGE = 10;

    private final BlogService blogService;
    private final PortfolioService portfolioService;

    public BlogController(BlogService blogService, PortfolioService portfolioService) {
        this.blogService = blogService;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/blog")
    public String blogList(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        var articlesPage = blogService.getPublishedArticles(PageRequest.of(page, ARTICLES_PER_PAGE));

        model.addAttribute("articlesPage", articlesPage);
        model.addAttribute("articles", articlesPage.getContent());
        model.addAttribute("pageTitle", "Blog | " + props.getDisplayName());
        model.addAttribute("pageDescription", "Articles and case studies from a full stack developer in Japan.");
        model.addAttribute("pageUrl", props.getSiteUrl() + "/blog");
        return "blog/list";
    }

    @GetMapping("/blog/{slug}")
    public String blogDetail(@PathVariable String slug, Model model) {
        PortfolioProperties props = portfolioService.getProperties();
        Article article = blogService.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found."));

        model.addAttribute("article", article);
        model.addAttribute("htmlContent", blogService.renderHtml(article.getContent()));
        model.addAttribute("pageTitle", article.getTitle() + " | Blog | " + props.getDisplayName());
        model.addAttribute("pageDescription",
                article.getExcerpt() != null ? article.getExcerpt() : article.getTitle());
        model.addAttribute("pageUrl", props.getSiteUrl() + "/blog/" + article.getSlug());
        return "blog/detail";
    }
}
