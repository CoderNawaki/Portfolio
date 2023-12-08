//add interactive features smooth scrolling ,form validation etc.

document.addEventListener("DOMContentLoaded",function(){
    const navLinks = document.querySelector('nav a');

    navLinks.forEach(link=>{
        link.addEventListener('click',smoothScroll);
    });

    function smoothScroll(e){
        e.preventDefault();

        const targetId = this.getAttribute('href').substring(1);

        const targetElement = document.getElementById(targetId);

        window.scrollTo({
            top:targetElement.offsetTop-60,//adjust the offset if you have a fixed header
            behavior:'smooth';
        });
    }
});