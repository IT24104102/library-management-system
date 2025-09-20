package lk.sliit.lms.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db-connection")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            // Test basic connection
            result.put("connected", true);
            result.put("url", connection.getMetaData().getURL());
            result.put("databaseName", connection.getCatalog());
            result.put("username", connection.getMetaData().getUserName());

            // Test query execution
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as table_count FROM information_schema.tables WHERE table_schema = 'lms'");
                if (rs.next()) {
                    result.put("tableCount", rs.getInt("table_count"));
                }
            }

            result.put("status", "SUCCESS");
            result.put("message", "Database connection is working properly");

        } catch (Exception e) {
            result.put("connected", false);
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @GetMapping("/tables")
    public Map<String, Object> listTables() {
        Map<String, Object> result = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SHOW TABLES");
                java.util.List<String> tables = new java.util.ArrayList<>();
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
                result.put("tables", tables);
                result.put("status", "SUCCESS");
            }
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
        }

        return result;
    }
}
