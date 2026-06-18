package com.codernawaki.portfolio;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArticleEngagementService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleCommentLikeRepository articleCommentLikeRepository;

    public ArticleEngagementService(ArticleLikeRepository articleLikeRepository,
                                    ArticleCommentRepository articleCommentRepository,
                                    ArticleCommentLikeRepository articleCommentLikeRepository) {
        this.articleLikeRepository = articleLikeRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.articleCommentLikeRepository = articleCommentLikeRepository;
    }

    @Transactional
    public Map<String, Object> toggleArticleLike(Long articleId, String ipAddress) {
        Optional<ArticleLike> existing = articleLikeRepository.findByArticleIdAndIpAddress(articleId, ipAddress);
        boolean liked;
        if (existing.isPresent()) {
            articleLikeRepository.delete(existing.get());
            liked = false;
        } else {
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setIpAddress(ipAddress);
            articleLikeRepository.save(like);
            liked = true;
        }
        int count = articleLikeRepository.countByArticleId(articleId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("count", count);
        return result;
    }

    public int getArticleLikeCount(Long articleId) {
        return articleLikeRepository.countByArticleId(articleId);
    }

    public boolean hasLikedArticle(Long articleId, String ipAddress) {
        return articleLikeRepository.findByArticleIdAndIpAddress(articleId, ipAddress).isPresent();
    }

    @Transactional
    public ArticleComment addComment(Long articleId, Long parentId, CommentForm form, String ipAddress) {
        if (parentId != null) {
            ArticleComment parent = articleCommentRepository.findById(parentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found."));
            if (!parent.getArticleId().equals(articleId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment does not belong to this article.");
            }
        }
        ArticleComment comment = new ArticleComment();
        comment.setArticleId(articleId);
        comment.setParentId(parentId);
        comment.setAuthor(form.getAuthor().trim());
        comment.setContent(form.getContent().trim());
        return articleCommentRepository.save(comment);
    }

    public List<ArticleComment> getCommentTree(Long articleId, String currentIp) {
        List<ArticleComment> allComments = articleCommentRepository.findByArticleIdOrderByCreatedAtAsc(articleId);
        if (allComments.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> likedCommentIds;
        if (currentIp != null) {
            likedCommentIds = allComments.stream()
                    .map(c -> articleCommentLikeRepository.findByCommentIdAndIpAddress(c.getId(), currentIp))
                    .filter(Optional::isPresent)
                    .map(opt -> opt.get().getCommentId())
                    .collect(Collectors.toSet());
        } else {
            likedCommentIds = Collections.emptySet();
        }

        Map<Long, Integer> likeCounts = new HashMap<>();
        for (ArticleComment c : allComments) {
            likeCounts.put(c.getId(), articleCommentLikeRepository.countByCommentId(c.getId()));
        }

        Map<Long, ArticleComment> commentMap = new HashMap<>();
        for (ArticleComment c : allComments) {
            c.setReplies(new ArrayList<>());
            c.setLikeCount(likeCounts.getOrDefault(c.getId(), 0));
            c.setLikedByCurrentUser(likedCommentIds.contains(c.getId()));
            commentMap.put(c.getId(), c);
        }

        List<ArticleComment> roots = new ArrayList<>();
        for (ArticleComment c : allComments) {
            if (c.getParentId() == null) {
                roots.add(c);
            } else {
                ArticleComment parent = commentMap.get(c.getParentId());
                if (parent != null) {
                    parent.getReplies().add(c);
                }
            }
        }
        return roots;
    }

    @Transactional
    public Map<String, Object> toggleCommentLike(Long commentId, String ipAddress) {
        if (!articleCommentRepository.existsById(commentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found.");
        }
        Optional<ArticleCommentLike> existing = articleCommentLikeRepository.findByCommentIdAndIpAddress(commentId, ipAddress);
        boolean liked;
        if (existing.isPresent()) {
            articleCommentLikeRepository.delete(existing.get());
            liked = false;
        } else {
            ArticleCommentLike like = new ArticleCommentLike();
            like.setCommentId(commentId);
            like.setIpAddress(ipAddress);
            articleCommentLikeRepository.save(like);
            liked = true;
        }
        int count = articleCommentLikeRepository.countByCommentId(commentId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("count", count);
        return result;
    }

    public List<ArticleComment> getAllCommentsForAdmin() {
        return articleCommentRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        ArticleComment comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found."));
        deleteCommentAndDescendants(comment);
    }

    private void deleteCommentAndDescendants(ArticleComment comment) {
        List<ArticleComment> allComments = articleCommentRepository.findByArticleIdOrderByCreatedAtAsc(comment.getArticleId());
        Set<Long> toDelete = new HashSet<>();
        collectDescendants(comment.getId(), allComments, toDelete);
        toDelete.add(comment.getId());
        articleCommentLikeRepository.deleteAllByCommentIdIn(toDelete);
        articleCommentRepository.deleteAllById(toDelete);
    }

    private void collectDescendants(Long parentId, List<ArticleComment> allComments, Set<Long> result) {
        for (ArticleComment c : allComments) {
            if (parentId.equals(c.getParentId())) {
                result.add(c.getId());
                collectDescendants(c.getId(), allComments, result);
            }
        }
    }
}
