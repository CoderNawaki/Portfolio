const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function toggleMenu() {
    const nav = document.querySelector("nav");
    if (nav) {
        nav.classList.toggle("active");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const navLinks = document.querySelectorAll("nav a");
    const form = document.getElementById("contactForm");
    const scrollToTopButton = document.getElementById("scrollToTopBtn");
    const formStatus = document.getElementById("formStatus");

    navLinks.forEach((link) => {
        link.addEventListener("click", smoothScroll);
    });

    if (form) {
        form.addEventListener("submit", validateAndSubmitForm);
    }

    if (scrollToTopButton) {
        window.addEventListener("scroll", toggleScrollButton);
        scrollToTopButton.addEventListener("click", () => {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
        toggleScrollButton();
    }

    function smoothScroll(event) {
        event.preventDefault();
        const targetId = this.getAttribute("href")?.substring(1);
        const targetElement = targetId ? document.getElementById(targetId) : null;

        if (!targetElement) {
            return;
        }

        window.scrollTo({
            top: targetElement.offsetTop - 60,
            behavior: "smooth"
        });
    }

    function toggleScrollButton() {
        const shouldShow = document.body.scrollTop > 10 || document.documentElement.scrollTop > 10;
        scrollToTopButton.style.display = shouldShow ? "block" : "none";
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
