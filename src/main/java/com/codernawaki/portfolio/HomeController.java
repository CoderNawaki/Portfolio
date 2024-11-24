public class HomeController{

    public String home(Form form,Model model){
        model.add("form",form);
        return "home";
    }
}