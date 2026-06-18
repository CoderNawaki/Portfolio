package com.codernawaki.portfolio;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class ArticleEngagementServiceTest {

    private ArticleLikeRepository articleLikeRepository;
    private ArticleCommentRepository articleCommentRepository;
    private ArticleCommentLikeRepository articleCommentLikeRepository;
    private ArticleEngagementService service;

    @BeforeEach
    void setUp() {
        articleLikeRepository = mock(ArticleLikeRepository.class);
        articleCommentRepository = mock(ArticleCommentRepository.class);
        articleCommentLikeRepository = mock(ArticleCommentLikeRepository.class);
        service = new ArticleEngagementService(articleLikeRepository, articleCommentRepository, articleCommentLikeRepository);
    }

    @Test
    void toggleArticleLikeCreatesLikeWhenNoneExists() {
        when(articleLikeRepository.findByArticleIdAndIpAddress(1L, "127.0.0.1")).thenReturn(Optional.empty());
        when(articleLikeRepository.countByArticleId(1L)).thenReturn(1);

        var result = service.toggleArticleLike(1L, "127.0.0.1");

        assertTrue((Boolean) result.get("liked"));
        assertEquals(1, result.get("count"));
        verify(articleLikeRepository).save(any(ArticleLike.class));
    }

    @Test
    void toggleArticleLikeRemovesLikeWhenExists() {
        when(articleLikeRepository.findByArticleIdAndIpAddress(1L, "127.0.0.1"))
                .thenReturn(Optional.of(new ArticleLike()));
        when(articleLikeRepository.countByArticleId(1L)).thenReturn(0);

        var result = service.toggleArticleLike(1L, "127.0.0.1");

        assertFalse((Boolean) result.get("liked"));
        assertEquals(0, result.get("count"));
        verify(articleLikeRepository).delete(any(ArticleLike.class));
    }

    @Test
    void getArticleLikeCountReturnsCount() {
        when(articleLikeRepository.countByArticleId(1L)).thenReturn(5);

        int count = service.getArticleLikeCount(1L);

        assertEquals(5, count);
    }

    @Test
    void hasLikedArticleReturnsTrueWhenExists() {
        when(articleLikeRepository.findByArticleIdAndIpAddress(1L, "127.0.0.1"))
                .thenReturn(Optional.of(new ArticleLike()));

        assertTrue(service.hasLikedArticle(1L, "127.0.0.1"));
    }

    @Test
    void addCommentSavesAndReturnsComment() {
        CommentForm form = new CommentForm();
        form.setAuthor("Tester");
        form.setContent("Great article!");

        ArticleComment saved = new ArticleComment();
        saved.setAuthor("Tester");
        saved.setContent("Great article!");
        when(articleCommentRepository.save(any())).thenReturn(saved);

        ArticleComment result = service.addComment(1L, null, form, "127.0.0.1");

        assertEquals("Tester", result.getAuthor());
        assertEquals("Great article!", result.getContent());
    }

    @Test
    void addReplySavesWithParentId() {
        ArticleComment parent = new ArticleComment();
        ReflectionTestUtils.setField(parent, "id", 5L);
        parent.setArticleId(1L);
        when(articleCommentRepository.findById(5L)).thenReturn(Optional.of(parent));

        CommentForm form = new CommentForm();
        form.setAuthor("Replier");
        form.setContent("Nice point!");

        ArticleComment saved = new ArticleComment();
        saved.setAuthor("Replier");
        saved.setContent("Nice point!");
        when(articleCommentRepository.save(any())).thenReturn(saved);

        ArticleComment result = service.addComment(1L, 5L, form, "127.0.0.1");

        assertEquals("Replier", result.getAuthor());
    }

    @Test
    void addReplyThrowsWhenParentNotFound() {
        when(articleCommentRepository.findById(99L)).thenReturn(Optional.empty());

        CommentForm form = new CommentForm();
        form.setAuthor("Tester");
        form.setContent("Test");

        assertThrows(ResponseStatusException.class, () -> service.addComment(1L, 99L, form, "127.0.0.1"));
    }

    @Test
    void toggleCommentLikeCreatesLikeWhenNoneExists() {
        when(articleCommentRepository.existsById(1L)).thenReturn(true);
        when(articleCommentLikeRepository.findByCommentIdAndIpAddress(1L, "127.0.0.1"))
                .thenReturn(Optional.empty());
        when(articleCommentLikeRepository.countByCommentId(1L)).thenReturn(1);

        var result = service.toggleCommentLike(1L, "127.0.0.1");

        assertTrue((Boolean) result.get("liked"));
        assertEquals(1, result.get("count"));
    }

    @Test
    void toggleCommentLikeThrowsWhenCommentNotFound() {
        when(articleCommentRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> service.toggleCommentLike(99L, "127.0.0.1"));
    }

    @Test
    void getCommentTreeReturnsEmptyWhenNoComments() {
        when(articleCommentRepository.findByArticleIdOrderByCreatedAtAsc(1L)).thenReturn(List.of());

        List<ArticleComment> result = service.getCommentTree(1L, "127.0.0.1");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteCommentDeletesCommentAndDescendants() {
        ArticleComment comment = new ArticleComment();
        ReflectionTestUtils.setField(comment, "id", 1L);
        comment.setArticleId(1L);
        when(articleCommentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(articleCommentRepository.findByArticleIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(comment));

        service.deleteComment(1L);

        verify(articleCommentRepository).deleteAllById(anySet());
    }

    @Test
    void getAllCommentsForAdminReturnsDescendingByCreatedAt() {
        ArticleComment c1 = new ArticleComment();
        when(articleCommentRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(c1));

        var result = service.getAllCommentsForAdmin();

        assertEquals(1, result.size());
    }
}
