document.addEventListener("DOMContentLoaded", () => {
    initArticleLikes();
    initCommentLikes();
    initCommentSubmission();
    initReplyToggle();
    initShareButtons();
});

function initArticleLikes() {
    const likeBtn = document.querySelector(".like-btn");
    if (!likeBtn) return;

    likeBtn.addEventListener("click", async () => {
        const slug = likeBtn.dataset.slug;
        try {
            const response = await fetch(`/blog/${slug}/like`, { method: "POST" });
            const data = await response.json();
            const countEl = likeBtn.querySelector(".like-count");
            if (countEl) countEl.textContent = data.count;
            likeBtn.dataset.liked = data.liked ? "true" : "false";
            likeBtn.classList.toggle("is-liked", data.liked);
        } catch (error) {
            console.error("Failed to toggle like:", error);
        }
    });
}

function initCommentLikes() {
    document.addEventListener("click", async (event) => {
        const btn = event.target.closest(".comment-like-btn");
        if (!btn) return;

        const commentId = btn.dataset.commentId;
        try {
            const response = await fetch(`/blog/comments/${commentId}/like`, { method: "POST" });
            const data = await response.json();
            const countEl = btn.querySelector(".like-count");
            if (countEl) countEl.textContent = data.count;
            btn.dataset.liked = data.liked ? "true" : "false";
            btn.classList.toggle("is-liked", data.liked);
        } catch (error) {
            console.error("Failed to toggle comment like:", error);
        }
    });
}

function initCommentSubmission() {
    document.addEventListener("submit", async (event) => {
        const form = event.target.closest(".comment-form");
        if (!form) return;

        event.preventDefault();

        const authorInput = form.querySelector(".comment-author-input");
        const contentInput = form.querySelector(".comment-content-input");
        const submitBtn = form.querySelector(".comment-submit-btn");

        if (!authorInput.value.trim() || !contentInput.value.trim()) return;

        const slug = form.dataset.slug;
        const parentId = form.dataset.parentId || null;

        submitBtn.disabled = true;
        submitBtn.textContent = "Posting...";

        try {
            const response = await fetch(`/blog/${slug}/comment`, {
                method: "POST",
                body: JSON.stringify({
                    author: authorInput.value.trim(),
                    content: contentInput.value.trim(),
                    parentId: parentId || null
                })
            });

            if (!response.ok) {
                console.error("Failed to post comment");
                submitBtn.disabled = false;
                submitBtn.textContent = parentId ? "Post Reply" : "Post Comment";
                return;
            }

            await refreshComments(slug);
            form.reset();
        } catch (error) {
            console.error("Failed to post comment:", error);
        }

        submitBtn.disabled = false;
        submitBtn.textContent = parentId ? "Post Reply" : "Post Comment";
    });
}

function initReplyToggle() {
    document.addEventListener("click", (event) => {
        const replyBtn = event.target.closest(".reply-toggle-btn");
        if (!replyBtn) return;

        const commentId = replyBtn.dataset.commentId;
        const wrapper = document.getElementById(`reply-form-${commentId}`);
        if (wrapper) {
            wrapper.style.display = wrapper.style.display === "none" ? "block" : "none";
        }
    });

    document.addEventListener("click", (event) => {
        const cancelBtn = event.target.closest(".reply-cancel-btn");
        if (!cancelBtn) return;

        const wrapper = cancelBtn.closest(".reply-form-wrapper");
        if (wrapper) {
            wrapper.style.display = "none";
            const form = wrapper.querySelector(".comment-form");
            if (form) form.reset();
        }
    });
}

async function refreshComments(slug) {
    try {
        const response = await fetch(`/blog/${slug}/comments`);
        if (!response.ok) return;
        const comments = await response.json();
        renderComments(comments, slug);
    } catch (error) {
        console.error("Failed to refresh comments:", error);
    }
}

function renderComments(comments, slug) {
    const container = document.querySelector(".comment-tree");
    const emptyState = document.querySelector(".comment-empty");

    if (!container && !emptyState) return;

    const section = document.querySelector(".comments-section");
    const existingTree = section.querySelector(".comment-tree");
    const existingEmpty = section.querySelector(".comment-empty");

    if (comments.length === 0) {
        if (existingTree) existingTree.remove();
        if (!existingEmpty) {
            const emptyDiv = document.createElement("div");
            emptyDiv.className = "comment-empty";
            emptyDiv.innerHTML = "<p>No comments yet. Be the first to share your thoughts!</p>";
            section.insertBefore(emptyDiv, section.querySelector(".add-comment"));
        }
        return;
    }

    if (existingEmpty) existingEmpty.remove();

    let tree = existingTree;
    if (!tree) {
        tree = document.createElement("div");
        tree.className = "comment-tree";
        section.insertBefore(tree, section.querySelector(".add-comment"));
    }

    tree.innerHTML = comments.map(c => renderCommentHtml(c)).join("");
    attachCommentLikeListeners(tree);
    attachReplyToggleListeners(tree);
}

function renderCommentHtml(comment) {
    const likedClass = comment.likedByCurrentUser ? " is-liked" : "";
    const repliesHtml = comment.replies && comment.replies.length > 0
        ? `<div class="comment-replies">${comment.replies.map(r => renderCommentHtml(r)).join("")}</div>`
        : "";

    return `<div class="comment">
        <div class="comment-body">
            <div class="comment-meta">
                <strong class="comment-author">${escapeHtml(comment.author)}</strong>
                <time class="comment-time">${formatTime(comment.createdAt)}</time>
            </div>
            <div class="comment-content">${escapeHtml(comment.content)}</div>
            <div class="comment-actions">
                <button class="engagement-btn comment-like-btn${likedClass}" data-comment-id="${comment.id}" data-liked="${comment.likedByCurrentUser}" type="button" aria-label="Like this comment">
                    <svg class="like-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    <span class="like-count">${comment.likeCount}</span>
                </button>
                <button class="reply-toggle-btn" data-comment-id="${comment.id}" type="button">Reply</button>
            </div>
            <div class="reply-form-wrapper" id="reply-form-${comment.id}" style="display:none;">
                <form class="comment-form" data-parent-id="${comment.id}" data-slug="${slug}">
                    <input type="text" class="comment-author-input" placeholder="Your name" required maxlength="100">
                    <textarea class="comment-content-input" placeholder="Write a reply..." required maxlength="2000"></textarea>
                    <div class="comment-form-actions">
                        <button type="submit" class="button button-primary comment-submit-btn">Post Reply</button>
                        <button type="button" class="button button-secondary reply-cancel-btn">Cancel</button>
                    </div>
                </form>
            </div>
        </div>
        ${repliesHtml}
    </div>`;
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

function formatTime(isoString) {
    if (!isoString) return "";
    const date = new Date(isoString);
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

function attachCommentLikeListeners(container) {
    container.querySelectorAll(".comment-like-btn").forEach(btn => {
        btn.addEventListener("click", async (event) => {
            const commentId = btn.dataset.commentId;
            try {
                const response = await fetch(`/blog/comments/${commentId}/like`, { method: "POST" });
                const data = await response.json();
                const countEl = btn.querySelector(".like-count");
                if (countEl) countEl.textContent = data.count;
                btn.dataset.liked = data.liked ? "true" : "false";
                btn.classList.toggle("is-liked", data.liked);
            } catch (error) {
                console.error("Failed to toggle comment like:", error);
            }
        });
    });
}

function attachReplyToggleListeners(container) {
    container.querySelectorAll(".reply-toggle-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const commentId = btn.dataset.commentId;
            const wrapper = document.getElementById(`reply-form-${commentId}`);
            if (wrapper) {
                wrapper.style.display = wrapper.style.display === "none" ? "block" : "none";
            }
        });
    });

    container.querySelectorAll(".reply-cancel-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const wrapper = btn.closest(".reply-form-wrapper");
            if (wrapper) {
                wrapper.style.display = "none";
                const form = wrapper.querySelector(".comment-form");
                if (form) form.reset();
            }
        });
    });
}

function initShareButtons() {
    document.querySelectorAll(".share-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const shareType = btn.dataset.share;
            const url = window.location.href;
            const title = document.title;

            switch (shareType) {
                case "copy":
                    navigator.clipboard.writeText(url).then(() => {
                        const original = btn.innerHTML;
                        btn.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>`;
                        setTimeout(() => { btn.innerHTML = original; }, 2000);
                    }).catch(() => {
                        console.error("Failed to copy link");
                    });
                    break;
                case "twitter":
                    window.open(`https://twitter.com/intent/tweet?text=${encodeURIComponent(title)}&url=${encodeURIComponent(url)}`, "_blank", "noopener,noreferrer");
                    break;
                case "linkedin":
                    window.open(`https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(url)}`, "_blank", "noopener,noreferrer");
                    break;
            }
        });
    });
}
