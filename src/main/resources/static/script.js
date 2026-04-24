const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

document.addEventListener("DOMContentLoaded", () => {
    initNavigation();
    initScrollToTop();
    initContactForm();
    initRevealAnimations();
});

function initNavigation() {
    const menuToggle = document.getElementById("menuToggle");
    const siteNav = document.getElementById("siteNav");
    const navLinks = document.querySelectorAll(".site-nav a");

    if (menuToggle && siteNav) {
        menuToggle.addEventListener("click", () => {
            const expanded = menuToggle.getAttribute("aria-expanded") === "true";
            menuToggle.setAttribute("aria-expanded", String(!expanded));
            siteNav.classList.toggle("is-open", !expanded);
        });
    }

    navLinks.forEach((link) => {
        link.addEventListener("click", (event) => {
            const targetId = link.getAttribute("href")?.replace("#", "");
            if (targetId && targetId.startsWith("/")) return; // Skip non-anchor links

            const targetElement = targetId ? document.getElementById(targetId) : null;
            if (!targetElement) return;

            event.preventDefault();
            targetElement.scrollIntoView({ behavior: "smooth", block: "start" });

            if (menuToggle && siteNav) {
                menuToggle.setAttribute("aria-expanded", "false");
                siteNav.classList.remove("is-open");
            }
        });
    });
}

function initScrollToTop() {
    const scrollToTopButton = document.getElementById("scrollToTopBtn");
    if (!scrollToTopButton) return;

    window.addEventListener("scroll", () => {
        scrollToTopButton.style.display = window.scrollY > 320 ? "inline-flex" : "none";
    });

    scrollToTopButton.addEventListener("click", () => {
        window.scrollTo({ top: 0, behavior: "smooth" });
    });
}

function initContactForm() {
    const form = document.getElementById("contactForm");
    if (!form) return;

    const fields = {
        name: { element: document.getElementById("name"), error: document.getElementById("nameError") },
        email: { element: document.getElementById("email"), error: document.getElementById("emailError") },
        message: { element: document.getElementById("message"), error: document.getElementById("messageError") }
    };
    const formStatus = document.getElementById("formStatus");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        if (!validateForm(fields, formStatus)) return;

        setFormStatus(formStatus, "Submitting...", false);
        const formData = {
            name: fields.name.element.value.trim(),
            email: fields.email.element.value.trim(),
            message: fields.message.element.value.trim()
        };

        try {
            const response = await fetch("/submitContactForm", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData)
            });

            const payload = await readResponsePayload(response);
            if (!response.ok) {
                applyFieldErrors(fields, payload.fieldErrors || {});
                setFormStatus(formStatus, buildErrorMessage(response, payload), true);
                return;
            }

            form.reset();
            clearFieldErrors(fields);
            setFormStatus(formStatus, payload.message || "Form submitted successfully.", false);
        } catch (error) {
            setFormStatus(formStatus, "Unable to submit the form right now. Please try again.", true);
        }
    });
}

async function readResponsePayload(response) {
    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return await response.json();
    }

    const text = await response.text();
    return {
        success: response.ok,
        message: text,
        fieldErrors: {}
    };
}

function buildErrorMessage(response, payload) {
    if (payload.message) {
        const retryAfter = response.headers.get("X-Rate-Limit-Retry-After-Seconds");
        if (response.status === 429 && retryAfter) {
            return `${payload.message} Try again in about ${retryAfter} seconds.`;
        }
        return payload.message;
    }

    return "Unable to submit the form.";
}

function validateForm(fields, formStatus) {
    clearFieldErrors(fields);
    let isValid = true;

    if (!fields.name.element.value.trim()) {
        showError(fields.name, "Please enter your name.");
        isValid = false;
    }
    if (!emailRegex.test(fields.email.element.value.trim())) {
        showError(fields.email, "Please enter a valid email address.");
        isValid = false;
    }
    if (!fields.message.element.value.trim()) {
        showError(fields.message, "Please enter a message.");
        isValid = false;
    }

    if (!isValid) setFormStatus(formStatus, "Please correct the highlighted fields.", true);
    return isValid;
}

function showError(field, message) {
    if (field.error) field.error.textContent = message;
    field.element.classList.add("has-error");
}

function applyFieldErrors(fields, errors) {
    Object.keys(fields).forEach(key => {
        if (errors[key]) showError(fields[key], errors[key]);
    });
}

function clearFieldErrors(fields) {
    Object.values(fields).forEach(field => {
        if (field.error) field.error.textContent = "";
        field.element.classList.remove("has-error");
    });
}

function setFormStatus(element, message, isError) {
    if (!element) return;
    element.textContent = message;
    element.className = isError ? "error" : "success";
}

function initRevealAnimations() {
    const revealElements = document.querySelectorAll(".reveal");
    if (!revealElements.length) return;

    if (!("IntersectionObserver" in window)) {
        revealElements.forEach(el => el.classList.add("is-visible"));
        return;
    }

    revealElements.forEach((el, i) => {
        el.style.transitionDelay = `${Math.min(i * 70, 280)}ms`;
    });

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add("is-visible");
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.16 });

    revealElements.forEach(el => observer.observe(el));
}
