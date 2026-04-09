package view;

import app.AppContext;
import facade.CriminalManagement;
import model.Criminal;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainUI {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        CriminalManagement management = AppContext.getInstance().getCriminalManagement();

        System.out.println("Enter Username:");
        String u = sc.nextLine();

        System.out.println("Enter Password:");
        String p = sc.nextLine();

        if (management.login(u, p)) {
            System.out.println("Login Successful");
            runMenu(sc, management);
        } else {
            System.out.println("Invalid Login");
        }
        sc.close();
    }

    private static void runMenu(Scanner sc, CriminalManagement management) {
        while (true) {
            System.out.println("\n===== Criminal Management Menu =====");
            System.out.println("1. Add Criminal");
            System.out.println("2. Search Criminal by ID");
            System.out.println("3. Update Criminal Crime Type");
            System.out.println("4. Delete Criminal");
            System.out.println("5. List All Criminals");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1:
                    addCriminalFlow(sc, management);
                    break;
                case 2:
                    searchCriminalFlow(sc, management);
                    break;
                case 3:
                    updateCrimeFlow(sc, management);
                    break;
                case 4:
                    deleteCriminalFlow(sc, management);
                    break;
                case 5:
                    listAllCriminalsFlow(management);
                    break;
                case 6:
                    System.out.println("Exiting system. Goodbye.");
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-6.");
            }
        }
    }

    private static void addCriminalFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID: ");
        int id = readInt(sc);

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter Age: ");
        int age = readInt(sc);

        System.out.print("Enter Crime Type: ");
        String crime = sc.nextLine().trim();

        boolean added = management.addCriminal(id, name, age, crime);
        if (added) {
            System.out.println("Criminal added successfully.");
        } else {
            System.out.println("Criminal ID already exists. Try a unique ID.");
        }
    }

    private static void searchCriminalFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID to search: ");
        int id = readInt(sc);
        Criminal c = management.searchCriminal(id);

        if (c == null) {
            System.out.println("No criminal found for ID " + id + ".");
            return;
        }

        printCriminal(c);
    }

    private static void updateCrimeFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID to update: ");
        int id = readInt(sc);

        System.out.print("Enter New Crime Type: ");
        String crime = sc.nextLine().trim();

        boolean updated = management.updateCrime(id, crime);
        if (updated) {
            System.out.println("Crime type updated successfully.");
        } else {
            System.out.println("Criminal ID not found.");
        }
    }

    private static void deleteCriminalFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID to delete: ");
        int id = readInt(sc);

        boolean deleted = management.deleteCriminal(id);
        if (deleted) {
            System.out.println("Criminal deleted successfully (with related case/evidence cleanup).");
        } else {
            System.out.println("Criminal ID not found.");
        }
    }

    private static void listAllCriminalsFlow(CriminalManagement management) {
        Collection<Criminal> criminals = management.getAllCriminals();
        if (criminals.isEmpty()) {
            System.out.println("No criminal records found.");
            return;
        }

        System.out.println("\n--- Criminal Records ---");
        for (Criminal c : criminals) {
            printCriminal(c);
        }
    }

    private static int readInt(Scanner sc) {
        while (true) {
            try {
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a valid number: ");
                sc.nextLine();
            }
        }
    }

    private static void printCriminal(Criminal c) {
        System.out.println(
            "ID: " + c.getCriminalId() +
            " | Name: " + c.getName() +
            " | Age: " + c.getAge() +
            " | Crime: " + c.getCrimeType()
        );
    }
}