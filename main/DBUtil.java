import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/balatro";
    private static final String USER = "root";
    private static final String PASSWORD = "BalatroLite244?!";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ensures driver is loaded
        } catch (ClassNotFoundException e) {
            System.err.println("⚠️ MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // In production, hash this
            ResultSet rs = stmt.executeQuery();

            boolean exists = rs.next();
            System.out.println("🔐 Login " + (exists ? "successful" : "failed") + " for user: " + username);
            return exists;

        } catch (SQLException e) {
            System.err.println("❌ DB error during login check");
            e.printStackTrace();
            return false;
        }
    }

    // Optional: register user (for future features)
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            int rows = stmt.executeUpdate();
            System.out.println("✅ User registered: " + username);
            return rows > 0;

        } catch (SQLIntegrityConstraintViolationException dup) {
            System.err.println("⚠️ Username already exists: " + username);
            return false;
        } catch (SQLException e) {
            System.err.println("❌ DB error during registration");
            e.printStackTrace();
            return false;
        }
    }
}
