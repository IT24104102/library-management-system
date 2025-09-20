package lk.sliit.lms.security;

import lk.sliit.lms.audit.AuditLog;
import lk.sliit.lms.audit.AuditLogRepository;
import lk.sliit.lms.auth.User;
import lk.sliit.lms.auth.UserRepository;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthEventsListener implements ApplicationListener<ApplicationEvent> {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuthEventsListener(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent success) {
            String username = success.getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(username);
            Long userId = userOpt.map(User::getId).orElse(null);
            AuditLog log = AuditLog.builder()
                    .actorUserId(userId)
                    .action("LOGIN_SUCCESS")
                    .targetType("USER")
                    .targetId(userId)
                    .timestamp(LocalDateTime.now())
                    .metadata(null)
                    .build();
            auditLogRepository.save(log);
        } else if (event instanceof AbstractAuthenticationFailureEvent failure) {
            String username = failure.getAuthentication() != null ? failure.getAuthentication().getName() : null;
            String reason = failure.getException() != null ? failure.getException().getClass().getSimpleName() : "UNKNOWN";
            String metadataJson = toJson("username", username, "reason", reason);
            AuditLog log = AuditLog.builder()
                    .actorUserId(null)
                    .action("LOGIN_FAILED")
                    .targetType("USER")
                    .targetId(null)
                    .timestamp(LocalDateTime.now())
                    .metadata(metadataJson)
                    .build();
            auditLogRepository.save(log);
        }
    }

    private String toJson(String k1, String v1, String k2, String v2) {
        return "{" +
                escapeJsonKey(k1) + ":" + escapeJsonValue(v1) + "," +
                escapeJsonKey(k2) + ":" + escapeJsonValue(v2) +
                "}";
    }

    private String escapeJsonKey(String key) {
        if (key == null) return "\"null\"";
        return "\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }

    private String escapeJsonValue(String val) {
        if (val == null) return "null";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
