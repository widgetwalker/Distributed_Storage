import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class LoginImpl extends UnicastRemoteObject implements Login {
    private Map<String, String> userDatabase;
    private static final String DATA_FILE = "users.txt";

    protected LoginImpl() throws RemoteException {
        super();
        userDatabase = new HashMap<>();
        loadDatabase();
    }

    private void loadDatabase() {
        File file = new File(DATA_FILE);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    userDatabase.put(parts[0], parts[1]);
                }
            }
            System.out.println("Database loaded: " + userDatabase.size() + " users.");
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }

    private void saveUser(String username, String password) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE, true))) {
            pw.println(username + "," + password);
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public synchronized String register(String username, String password) throws RemoteException {
        String clientHost = "";
        try {
            clientHost = getClientHost();
        } catch (Exception ignored) {
        }
        System.out.println("Register request from " + clientHost + " for user: " + username);

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "Error: Username and password cannot be empty.";
        }
        if (userDatabase.containsKey(username)) {
            return "Error: Username '" + username + "' is already taken.";
        }

        userDatabase.put(username, password);
        saveUser(username, password);
        return "Registration successful for " + username;
    }

    @Override
    public synchronized boolean authenticate(String username, String password) throws RemoteException {
        String clientHost = "";
        try {
            clientHost = getClientHost();
        } catch (Exception ignored) {
        }
        System.out.println("Login attempt from " + clientHost + " for user: " + username);

        if (username == null || password == null)
            return false;

        String storedPassword = userDatabase.get(username);
        if (storedPassword != null && storedPassword.equals(password)) {
            System.out.println("SUCCESS: User '" + username + "' authenticated.");
            return true;
        }
        System.out.println("FAILED: Invalid credentials for user '" + username + "'.");
        return false;
    }
}
