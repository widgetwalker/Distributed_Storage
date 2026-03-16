import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            // Use server IP/hostname from command-line if provided, otherwise localhost
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Server IP Address: ");
            String host = scanner.nextLine().trim();
            if (host.isEmpty()) {
                host = "localhost";
            }

            Arithmetic stub = (Arithmetic) Naming.lookup("rmi://" + host + "/ArithmeticService");

            System.out.println("Connected to ArithmeticService (type 'exit' to quit).");
            while (true) {
                System.out.print("\nChoose operation [add, subtract, multiply, divide, exit]: ");
                String op = scanner.next().trim().toLowerCase();
                if (op.equals("exit") || op.equals("quit")) {
                    System.out.println("Exiting.");
                    break;
                }

                try {
                    System.out.print("Enter first number: ");
                    double a = Double.parseDouble(scanner.next().trim());
                    System.out.print("Enter second number: ");
                    double b = Double.parseDouble(scanner.next().trim());

                    double result = stub.compute(op, a, b);
                    System.out.println("Result: " + result);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid number. Please try again.");
                } catch (Exception e) {
                    System.out.println("Error calling remote method: " + e.getMessage());
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
