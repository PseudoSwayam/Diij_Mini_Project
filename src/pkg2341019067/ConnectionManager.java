package pkg2341019067;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionManager {
    private static final String URL = "jdbc:derby:SwayamDB;create=true";
    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Derby driver not found on classpath: " + e.getMessage());
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:SwayamDB;shutdown=true");
        } catch (SQLException se) {
            // Derby throws SQLState 08006 or error code 50000 on successful shutdown
            // swallow it
        }
    }
}
