package lk.sliit.lms.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPageController {

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }
}

