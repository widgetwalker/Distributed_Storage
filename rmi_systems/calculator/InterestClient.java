package calculator;

import java.rmi.Naming;
import java.util.Scanner;

public class InterestClient {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter Server IP Address (leave empty for localhost): ");
            String host = scanner.nextLine().trim();
            if (host.isEmpty()) {
                host = "localhost";
            }

            // Look up the remote object
            InterestCalculator stub = (InterestCalculator) Naming.lookup("rmi://" + host + "/InterestCalculator");
            System.out.println("Connected to InterestCalculator service.");

            while (true) {
                System.out.println("\n========== Interest Calculator ==========");
                System.out.println("1. Simple Interest");
                System.out.println("2. Compound Interest");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                if (!scanner.hasNextLine())
                    break;
                String choice = scanner.nextLine().trim();

                if (choice.equals("3") || choice.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting. Goodbye!");
                    break;
                }

                if (!choice.equals("1") && !choice.equals("2")) {
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
                    continue;
                }

                // Common inputs for both SI and CI
                System.out.print("Enter Principal amount (P): ");
                double principal = Double.parseDouble(scanner.nextLine().trim());

                System.out.print("Enter Rate of interest per year (R in %): ");
                double rate = Double.parseDouble(scanner.nextLine().trim());

                System.out.print("Enter Time period (T in years): ");
                double time = Double.parseDouble(scanner.nextLine().trim());

                if (choice.equals("1")) {
                    System.out.println("\n[Client] Sending Simple Interest request to server...");
                    double si = stub.calculateSimpleInterest(principal, rate, time);
                    System.out.println("------------------------------------------");
                    System.out.println("  Principal (P) : " + principal);
                    System.out.println("  Rate      (R) : " + rate + "%");
                    System.out.println("  Time      (T) : " + time + " years");
                    System.out.println("  Simple Interest = " + si);
                    System.out.println("  Total Amount    = " + (principal + si));
                    System.out.println("------------------------------------------");

                } else {
                    System.out.println("\n[Client] Sending Compound Interest request to server...");
                    double ci = stub.calculateCompoundInterest(principal, rate, time);
                    System.out.println("------------------------------------------");
                    System.out.println("  Principal (P) : " + principal);
                    System.out.println("  Rate      (R) : " + rate + "%");
                    System.out.println("  Time      (T) : " + time + " years");
                    System.out.println("  Compound Interest = " + ci);
                    System.out.println("  Total Amount      = " + (principal + ci));
                    System.out.println("------------------------------------------");
                }
            }

            scanner.close();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number entered. Please enter a valid numeric value.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
