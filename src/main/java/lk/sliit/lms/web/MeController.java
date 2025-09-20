package lk.sliit.lms.web;

import lk.sliit.lms.auth.DbUserDetails;
import lk.sliit.lms.auth.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(@AuthenticationPrincipal Object principal) {
        Map<String, Object> out = new HashMap<>();
        if (principal instanceof DbUserDetails dud) {
            User u = dud.getUser();
            out.put("email", u.getEmail());
            out.put("name", u.getName());
            out.put("roles", u.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toList()));
        } else {
            out.put("principal", String.valueOf(principal));
        }
        return out;
    }
}

