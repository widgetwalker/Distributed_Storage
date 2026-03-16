package login;

import java.rmi.Naming;
import java.util.Scanner;

public class LoginClient {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Server IP Address (leave empty for localhost): ");
            String host = scanner.nextLine().trim();
            if (host.isEmpty()) {
                host = "localhost";
            }

            Login stub = (Login) Naming.lookup("rmi://" + host + "/LoginService");

            System.out.println("Connected to LoginService.");
            while (true) {
                System.out.println("\nOptions:");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                if (!scanner.hasNextLine()) {
                    break;
                }
                String choice = scanner.nextLine().trim();

                if (choice.equals("3") || choice.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting.");
                    break;
                } else if (choice.equals("1")) {
                    System.out.print("Enter new username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Enter new password: ");
                    String password = scanner.nextLine().trim();

                    String response = stub.register(username, password);
                    System.out.println("Server Response: " + response);
                } else if (choice.equals("2")) {
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine().trim();

                    boolean isAuthenticated = stub.authenticate(username, password);
                    if (isAuthenticated) {
                        System.out.println("Server Response: Authentication successful! Welcome, " + username + ".");
                    } else {
                        System.out.println("Server Response: Authentication failed. Invalid username or password.");
                    }
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
