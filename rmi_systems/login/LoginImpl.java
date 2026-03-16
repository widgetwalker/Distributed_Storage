package login;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.RemoteServer;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LoginImpl extends UnicastRemoteObject implements Login {

    private Map<String, String> userDatabase;
    private static final String DATA_FILE = "login/users.txt";

    protected LoginImpl() throws RemoteException {
        super();
        userDatabase = new HashMap<>();
        loadDatabase();
    }

    private void loadDatabase() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 2);
                    if (parts.length == 2) {
                        userDatabase.put(parts[0], parts[1]);
                    }
                }
                System.out.println(">> Loaded " + userDatabase.size() + " users from database file.");
            } catch (IOException e) {
                System.out.println(">> Error reading database file: " + e.getMessage());
            }
        }
    }

    private void saveUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(username + "," + password);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(">> Error saving user to database file: " + e.getMessage());
        }
    }

    private String getClient() {
        try {
            return RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            return "Unknown Host";
        }
    }

    @Override
    public String register(String username, String password) throws RemoteException {
        System.out.println("\n--------------------------------------------------");
        System.out.println(">> Client [" + getClient() + "] connected");
        System.out.println(">> Action Selected : REGISTER");
        System.out.println(">> Username Details: " + username);
        System.out.println(">> Password Details: " + password);

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println(">> Result          : FAILED - Empty Username or Password");
            return "Username and password cannot be empty.";
        }

        if (userDatabase.containsKey(username)) {
            System.out.println(">> Result          : FAILED - Username '" + username + "' is already taken");
            return "Registration failed: Username '" + username + "' is already taken.";
        }

        userDatabase.put(username, password);
        saveUser(username, password);
        System.out.println(">> Result          : SUCCESS - User '" + username + "' successfully registered");
        return "Registration successful for user '" + username + "'.";
    }

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        System.out.println("\n--------------------------------------------------");
        System.out.println(">> Client [" + getClient() + "] connected");
        System.out.println(">> Action Selected : LOGIN");
        System.out.println(">> Username Details: " + username);
        System.out.println(">> Password Details: " + password);

        if (username == null || password == null) {
            System.out.println(">> Result          : FAILED - Empty Username or Password");
            return false;
        }

        String storedPassword = userDatabase.get(username);
        boolean success = storedPassword != null && storedPassword.equals(password);

        if (success) {
            System.out.println(">> Result          : SUCCESS - User '" + username + "' authenticated");
        } else {
            System.out.println(">> Result          : FAILED - Invalid credentials for '" + username + "'");
        }
        return success;
    }
}
