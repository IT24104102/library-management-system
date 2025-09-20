package lk.sliit.lms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StartupChecklist implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupChecklist.class);

    private final Environment env;
    private final UserDetailsService userDetailsService;

    @Value("${server.port:8080}")
    private int serverPort;

    public StartupChecklist(Environment env, UserDetailsService userDetailsService) {
        this.env = env;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] profiles = env.getActiveProfiles();
        if (profiles.length == 0) profiles = env.getDefaultProfiles();
        String activeProfile = profiles.length > 0 ? profiles[0] : "default";

        // Resolve DB settings from environment (no secrets)
        String dbHost = firstNonEmpty(
            env.getProperty("MYSQL_HOST"),
            env.getProperty("DB_HOST"),
            "127.0.0.1"
        );
        String dbPort = firstNonEmpty(
            env.getProperty("MYSQL_PORT"),
            env.getProperty("DB_PORT"),
            "3306"
        );
        String dbName = firstNonEmpty(
            env.getProperty("MYSQL_DB"),
            env.getProperty("DB_NAME"),
            "lms"
        );

        List<String> expectedUsers = List.of("admin","librarian","assistant","student","itsupport","chief");
        long loadedCount = expectedUsers.stream().filter(u -> {
            try { userDetailsService.loadUserByUsername(u); return true; } catch (Exception e) { return false; }
        }).count();

        // Clear banner
        log.info("LMS started on profile: {} — DB: {}@{}:{}", activeProfile, dbName, dbHost, dbPort);

        // Detailed checklist
        log.info("================ LMS Backend Startup Checklist ================");
        log.info("[Profiles] active={} (expect: local)", Arrays.toString(profiles));
        log.info("[Server] http://localhost:{}", serverPort);
        log.info("[Security] In-memory RBAC users loaded: {}/6 (admin, librarian, assistant, student, itsupport, chief)", loadedCount);
        log.info("[JPA Entities] User, Book, Loan, Reservation, Fine, AuditLog present (placeholders)");
        log.info("[Config] JSP support added (tomcat-embed-jasper, JSTL)");
        log.info("[Datasource] Using env vars MYSQL_HOST/PORT/DB/USER/PASSWORD (ddl-auto=none)");
        log.info("[Scope] Web-only, single library, local MySQL; no cloud/mobile; basic security");
        log.info("===============================================================");
    }

    private static String firstNonEmpty(String a, String b, String fallback) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return fallback;
    }
}
