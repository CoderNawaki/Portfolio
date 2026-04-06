const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

document.addEventListener("DOMContentLoaded", () => {
    const menuToggle = document.getElementById("menuToggle");
    const siteNav = document.getElementById("siteNav");
    const navLinks = document.querySelectorAll(".site-nav a");
    const form = document.getElementById("contactForm");
    const scrollToTopButton = document.getElementById("scrollToTopBtn");
    const formStatus = document.getElementById("formStatus");
    const revealElements = document.querySelectorAll(".reveal");
    const fieldElements = {
        name: document.getElementById("name"),
        email: document.getElementById("email"),
        message: document.getElementById("message")
    };
    const fieldErrorElements = {
        name: document.getElementById("nameError"),
        email: document.getElementById("emailError"),
        message: document.getElementById("messageError")
    };

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
            const targetElement = targetId ? document.getElementById(targetId) : null;

            if (!targetElement) {
                return;
            }

            event.preventDefault();
            targetElement.scrollIntoView({ behavior: "smooth", block: "start" });

            if (menuToggle && siteNav) {
                menuToggle.setAttribute("aria-expanded", "false");
                siteNav.classList.remove("is-open");
            }
        });
    });

    if (scrollToTopButton) {
        window.addEventListener("scroll", () => {
            const shouldShow = window.scrollY > 320;
            scrollToTopButton.style.display = shouldShow ? "inline-flex" : "none";
        });

        scrollToTopButton.addEventListener("click", () => {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
    }

    if (form) {
        form.addEventListener("submit", validateAndSubmitForm);
    }

    if ("IntersectionObserver" in window && revealElements.length > 0) {
        revealElements.forEach((element, index) => {
            element.style.transitionDelay = `${Math.min(index * 70, 280)}ms`;
        });

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    entry.target.classList.add("is-visible");
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.16 });

        revealElements.forEach((element) => observer.observe(element));
    } else {
        revealElements.forEach((element) => element.classList.add("is-visible"));
    }

    async function validateAndSubmitForm(event) {
        event.preventDefault();

        const formData = {
            name: fieldElements.name?.value.trim() ?? "",
            email: fieldElements.email?.value.trim() ?? "",
            message: fieldElements.message?.value.trim() ?? ""
        };

        clearFieldErrors();

        if (!validateName(formData.name) || !validateEmail(formData.email) || !validateMessage(formData.message)) {
            return;
        }

        setFormStatus("Submitting...", false);

        try {
            const response = await fetch("/submitContactForm", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(formData)
            });

            const payload = await response.json();

            if (!response.ok) {
                applyFieldErrors(payload.fieldErrors || {});
                setFormStatus(payload.message || "Unable to submit the form.", true);
                return;
            }

            form.reset();
            clearFieldErrors();
            setFormStatus(payload.message || "Form submitted successfully.", false);
        } catch (error) {
            setFormStatus("Unable to submit the form right now. Please try again.", true);
        }
    }

    function validateName(nameValue) {
        if (nameValue === "") {
            applyFieldErrors({ name: "Please enter your name." });
            setFormStatus("Please enter your name.", true);
            return false;
        }
        return true;
    }

    function validateEmail(emailValue) {
        if (!emailRegex.test(emailValue)) {
            applyFieldErrors({ email: "Please enter a valid email address." });
            setFormStatus("Please enter a valid email address.", true);
            return false;
        }
        return true;
    }

    function validateMessage(messageValue) {
        if (messageValue === "") {
            applyFieldErrors({ message: "Please enter a message." });
            setFormStatus("Please enter a message.", true);
            return false;
        }
        return true;
    }

    function applyFieldErrors(fieldErrors) {
        Object.entries(fieldErrorElements).forEach(([fieldName, errorElement]) => {
            const message = fieldErrors[fieldName] || "";

            if (errorElement) {
                errorElement.textContent = message;
            }

            fieldElements[fieldName]?.classList.toggle("has-error", message !== "");
        });
    }

    function clearFieldErrors() {
        applyFieldErrors({
            name: "",
            email: "",
            message: ""
        });
    }

    function setFormStatus(message, isError) {
        if (!formStatus) {
            return;
        }

        formStatus.textContent = message;
        formStatus.classList.toggle("error", isError);
        formStatus.classList.toggle("success", !isError);
    }
});
