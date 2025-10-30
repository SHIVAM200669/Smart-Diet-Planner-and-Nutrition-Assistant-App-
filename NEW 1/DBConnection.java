import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // The connection URL for MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/dietdb?serverTimezone=UTC";

    // Your database username (change if needed)
    private static final String USER = "root";

    // Your database password
    private static final String PASS = "Poonam@1"; // <-- Make sure this is your correct password

    /**
     * Establishes and returns a connection to the database.
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager automatically finds the driver if the JAR is in the classpath
        return DriverManager.getConnection(URL, USER, PASS);
    }
}