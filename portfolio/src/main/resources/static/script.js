const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

document.addEventListener("DOMContentLoaded", () => {
    const menuToggle = document.getElementById("menuToggle");
    const siteNav = document.getElementById("siteNav");
    const navLinks = document.querySelectorAll(".site-nav a");
    const form = document.getElementById("contactForm");
    const scrollToTopButton = document.getElementById("scrollToTopBtn");
    const formStatus = document.getElementById("formStatus");

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

    async function validateAndSubmitForm(event) {
        event.preventDefault();

        const formData = {
            name: document.getElementById("name")?.value.trim() ?? "",
            email: document.getElementById("email")?.value.trim() ?? "",
            message: document.getElementById("message")?.value.trim() ?? ""
        };

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
                setFormStatus(payload.message || "Unable to submit the form.", true);
                return;
            }

            form.reset();
            setFormStatus(payload.message || "Form submitted successfully.", false);
        } catch (error) {
            setFormStatus("Unable to submit the form right now. Please try again.", true);
        }
    }

    function validateName(nameValue) {
        if (nameValue === "") {
            setFormStatus("Please enter your name.", true);
            return false;
        }
        return true;
    }

    function validateEmail(emailValue) {
        if (!emailRegex.test(emailValue)) {
            setFormStatus("Please enter a valid email address.", true);
            return false;
        }
        return true;
    }

    function validateMessage(messageValue) {
        if (messageValue === "") {
            setFormStatus("Please enter a message.", true);
            return false;
        }
        return true;
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
