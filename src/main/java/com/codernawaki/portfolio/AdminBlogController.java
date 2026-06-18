package com.codernawaki.portfolio;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminBlogController {

    private static final int ARTICLES_PER_PAGE = 10;

    private final BlogService blogService;
    private final ArticleEngagementService engagementService;

    public AdminBlogController(BlogService blogService, ArticleEngagementService engagementService) {
        this.blogService = blogService;
        this.engagementService = engagementService;
    }

    @GetMapping("/admin/articles")
    public String articles(
            @RequestParam(required = false) Long selected,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        int safePage = Math.max(page, 0);
        Page<Article> articlesPage = blogService.getAllArticles(PageRequest.of(safePage, ARTICLES_PER_PAGE));

        model.addAttribute("articlesPage", articlesPage);
        model.addAttribute("articles", articlesPage.getContent());

        Article selectedArticle = null;
        if (selected != null) {
            selectedArticle = blogService.findById(selected).orElse(null);
        }
        model.addAttribute("selectedArticle", selectedArticle);
        model.addAttribute("articleForm", selectedArticle != null ? toForm(selectedArticle) : new ArticleForm());

        model.addAttribute("pageTitle", "Manage Articles | Admin");
        return "admin/articles";
    }

    @PostMapping("/admin/articles")
    public String createArticle(
            @Valid @ModelAttribute("articleForm") ArticleForm form,
            RedirectAttributes redirectAttributes) {
        blogService.createArticle(form);
        redirectAttributes.addFlashAttribute("adminMessage", "Article created.");
        return "redirect:/admin/articles";
    }

    @PostMapping("/admin/articles/{articleId}")
    public String updateArticle(
            @PathVariable long articleId,
            @Valid @ModelAttribute("articleForm") ArticleForm form,
            RedirectAttributes redirectAttributes) {
        blogService.updateArticle(articleId, form);
        redirectAttributes.addFlashAttribute("adminMessage", "Article updated.");
        redirectAttributes.addAttribute("selected", articleId);
        return "redirect:/admin/articles";
    }

    @PostMapping("/admin/articles/{articleId}/delete")
    public String deleteArticle(
            @PathVariable long articleId,
            RedirectAttributes redirectAttributes) {
        blogService.deleteArticle(articleId);
        redirectAttributes.addFlashAttribute("adminMessage", "Article deleted.");
        return "redirect:/admin/articles";
    }

    @GetMapping("/admin/comments")
    public String comments(Model model) {
        List<ArticleComment> comments = engagementService.getAllCommentsForAdmin();
        model.addAttribute("comments", comments);
        model.addAttribute("pageTitle", "Manage Comments | Admin");
        return "admin/comments";
    }

    @PostMapping("/admin/comments/{commentId}/delete")
    public String deleteComment(@PathVariable long commentId, RedirectAttributes redirectAttributes) {
        engagementService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute("adminMessage", "Comment deleted.");
        return "redirect:/admin/comments";
    }

    private ArticleForm toForm(Article article) {
        ArticleForm form = new ArticleForm();
        form.setTitle(article.getTitle());
        form.setContent(article.getContent());
        form.setExcerpt(article.getExcerpt());
        form.setTags(article.getTags());
        form.setSlug(article.getSlug());
        form.setPublish(article.getStatus() == ArticleStatus.PUBLISHED);
        return form;
    }
}
