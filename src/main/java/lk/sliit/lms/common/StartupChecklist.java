package lk.sliit.lms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupChecklist implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupChecklist.class);

    private final Environment env;

    @Value("${server.port:8081}")
    private int serverPort;

    public StartupChecklist(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] profiles = env.getActiveProfiles();
        if (profiles.length == 0) profiles = env.getDefaultProfiles();
        String activeProfile = profiles.length > 0 ? profiles[0] : "default";

        String dbHost = firstNonEmpty(env.getProperty("MYSQL_HOST"), "127.0.0.1");
        String dbPort = firstNonEmpty(env.getProperty("MYSQL_PORT"), "3307");
        String dbName = firstNonEmpty(env.getProperty("MYSQL_DB"), "lms");

        if ("local".equalsIgnoreCase(activeProfile)) {
            log.info("LMS started (profile=local, DB={}@{}:{}, PORT={})", dbName, dbHost, dbPort, serverPort);
            String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&connectionCollation=utf8mb4_0900_ai_ci&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false",
                dbHost, dbPort, dbName
            );
            log.info("[Datasource] JDBC URL: {}", jdbcUrl);
        } else {
            log.info("LMS started (profile={}, DB={}@{}:{})", activeProfile, dbName, dbHost, dbPort);
        }

        log.info("================ LMS Backend Startup Checklist ================");
        log.info("[Profiles] active={} (expect: local)", Arrays.toString(profiles));
        log.info("[Server] http://localhost:{}", serverPort);
        log.info("[Security] Session form login; CSRF DISABLED for demo; simple RBAC");
        log.info("[JPA Entities] User, Book, Loan, Reservation, Fine, AuditLog present (placeholders)");
        log.info("[Config] JSP support added (tomcat-embed-jasper, JSTL)");
        log.info("[Datasource] Using env vars MYSQL_HOST/PORT/DB/USER/PASSWORD (ddl-auto=none)");
        log.info("[Scope] Web-only, single library, local MySQL; no cloud/mobile; basic security");
        log.info("===============================================================");
    }

    private static String firstNonEmpty(String a, String fallback) {
        return (a != null && !a.isBlank()) ? a : fallback;
    }
}
