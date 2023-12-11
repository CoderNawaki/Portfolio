

@RestController
class ContactController{

    @PostMapping("/submitContactForm")
    public String submitContactForm(@RequestBody ContactForm contactForm){

        if(isValid(contactForm)){
            return "Form submitted successfully";
        }else{
            return "Invalid form data.Please check your input.";
        }
    }

    private boolean isValid(ContactForm contactForm){
        return contactForm.getName()!=null && !contactForm.getName().trim().isEmpty()
                && contactForm.getEmail()!=null && !contactForm.getEmail().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")
                && contactForm.getMessage()!=null && !contactForm.getMessage().trim().isEmpty();
    }
}