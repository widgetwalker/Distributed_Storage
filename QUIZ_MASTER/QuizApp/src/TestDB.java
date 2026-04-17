import com.quizapp.server.DatabaseManager;
import com.quizapp.shared.User;

public class TestDB {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        System.out.println("Fetching user 'dheer'...");
        User u = db.getUserByUsername("dheer");
        if (u == null) {
            System.out.println("User not found, creating...");
            u = db.createUser("dheer", "");
            if (u == null) {
                System.out.println("Creation returned null!");
            } else {
                System.out.println("Created: " + u.getId() + ", " + u.getUsername());
            }
        } else {
            System.out.println("Found: " + u.getId() + ", " + u.getUsername());
        }
    }
}
