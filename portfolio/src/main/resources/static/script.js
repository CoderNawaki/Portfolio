//add interactive features smooth scrolling ,form validation etc.

document.addEventListener("DOMContentLoaded",function(){
    const navLinks = document.querySelector('nav a');

    navLinks.foreach(link=>{
        link.addEventListener('click',smoothScroll);
    });

    function smoothScroll(e){
        e.preventDefault();

        const targetId = this.getAttribute('href').substring(1);

        const targetElement = document.getElementById(targetId);

        window.scrollTo({
            top:targetElement.offsetTop-60,//adjust the offset if you have a fixed header
            behavior:'smooth'
        });
    }
});


//form validation
document.addEventListener("DOMContentLoaded",function(){
 const form = document.getElementById('contactForm');
 form.addEventListener('submit',validateForm);

 function validateForm(e){
 e.preventDefault();

 const nameInput = document.getElementById('name');
 const emailInput= document.getElementById('email');
 const messageInput = document.getElementById('message');

 if(validateName(nameInput) && validateEmail(emailInput) && validateMessage(messageInput)){
    //submit the form (you can add a ajax request here or let the form submit naturally.
    alert('Form submitted successfully.');

     }
  }


 function validateName(nameInput){
 const nameValue = nameInput.value.trim();
     if(nameValue ===''){
        alert('Please enter your name.');
        return false;
     }
     return true;
 }

 function validateEmail(emailInput){
    const emailValue = emailInput.value.trim();

    const emailRegex = /^[^¥s@]+@[¥s@]+¥.[^¥s@]+$/;

    if(!emailRegex.test(emailValue)){
        alert('Please enter a  valid email address.');
        return false;
    }
    return true;
 }

 function validateMessage(messageInput){
 const messageValue = messageInput.value.trim();
    if(messageValue===''){
    alert('Please enter a message.');
        return false;
    }
    return true;
 }

});

//scroll event button to top

document.addEventListener("DOMContentLoaded", function () {
    // Your existing JavaScript code

    // Scroll-to-top button functionality
    const scrollToTopButton = document.getElementById("scrollToTopBtn");

    // Show the button when the user scrolls down 20px from the top
    window.scroll = function () {
        scrollFunction();
    };

    function scrollFunction() {
        if (document.body.scrollTop > 10 || document.documentElement.scrollTop > 10) {
            scrollToTopButton.style.display = "block";
        } else {
            scrollToTopButton.style.display = "none";
        }
    }

    // Scroll to the top of the page when the button is clicked
    scrollToTopButton.addEventListener("click", function () {
        document.body.scrollTop = 0;
        document.documentElement.scrollTop = 0;
    });
});