package lk.sliit.lms.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoansController {
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("ok", true, "area", "loans");
    }
}

