package lk.sliit.lms.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationsController {
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("ok", true, "area", "reservations");
    }
}

