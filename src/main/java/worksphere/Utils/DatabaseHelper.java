package worksphere.Utils;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:h2:./worksphere_db;AUTO_SERVER=TRUE";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    public static void initializeDatabase() {
        String[] queries = {
                "CREATE TABLE IF NOT EXISTS attendance (id INT AUTO_INCREMENT PRIMARY KEY, emp_id VARCHAR(50), date DATE, time_in VARCHAR(20), status VARCHAR(20))",
                "CREATE TABLE IF NOT EXISTS leave_requests (id INT AUTO_INCREMENT PRIMARY KEY, emp_id VARCHAR(50), type VARCHAR(20), date VARCHAR(20), reason TEXT, status VARCHAR(20))",
                "CREATE TABLE IF NOT EXISTS tasks (id INT AUTO_INCREMENT PRIMARY KEY, emp_id VARCHAR(50), title VARCHAR(100), project VARCHAR(100), priority INT)" // 1=High,
                                                                                                                                                                   // 2=Med,
                                                                                                                                                                   // 3=Low
        };
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String q : queries)
                stmt.execute(q);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}