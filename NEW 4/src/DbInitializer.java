import java.sql.*;

public class DbInitializer {
    public static void run() {
        ensureDatabase();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;
            Statement st = conn.createStatement();

            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(100) UNIQUE NOT NULL," +
                    "password VARCHAR(100) NOT NULL," +
                    "age INT DEFAULT NULL," +
                    "weight FLOAT DEFAULT NULL," +
                    "height FLOAT DEFAULT NULL," +
                    "preferences TEXT DEFAULT NULL" +
                    ")");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS gps_tracking (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(100) NOT NULL," +
                    "activity_type VARCHAR(50) NOT NULL," +
                    "latitude DOUBLE NOT NULL," +
                    "longitude DOUBLE NOT NULL," +
                    "distance_km DOUBLE DEFAULT 0," +
                    "calories_burned DOUBLE DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS goals (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(100) NOT NULL," +
                    "goal_title VARCHAR(255) NOT NULL," +
                    "target VARCHAR(255) DEFAULT NULL," +
                    "deadline DATE DEFAULT NULL," +
                    "status VARCHAR(50) DEFAULT 'pending'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT IGNORE INTO users (username, password, age, weight, height, preferences) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, "admin");
                ps.setString(2, "admin");
                ps.setInt(3, 25);
                ps.setFloat(4, 70f);
                ps.setFloat(5, 170f);
                ps.setString(6, "Demo user");
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    private static void ensureDatabase() {
        String dbName = "food_diet_planner";
        String url = "jdbc:mysql://localhost:3306/?permitMysqlScheme";
        String user = "root";
        String pass = "Poonam@1";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(url, user, pass)) {
                try (Statement s = c.createStatement()) {
                    s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
