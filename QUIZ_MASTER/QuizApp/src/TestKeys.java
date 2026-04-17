import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestKeys {
    public static void main(String[] args) throws Exception {
        String DB_URL = "jdbc:sqlite::memory:";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT)");
            
            String sql = "INSERT INTO users(username) VALUES(?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "test");
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Key: " + rs.getInt(1));
                } else {
                    System.out.println("NO KEYS RETURNED! BUG CONFIRMED.");
                }
            }
        }
    }
}
