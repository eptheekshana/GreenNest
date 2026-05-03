import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Handles requests to "localhost:8080/login"
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Looks for login.html in resources/templates/
    }

    // Handles requests to "localhost:8080/register"
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Looks for register.html in resources/templates/
    }
}