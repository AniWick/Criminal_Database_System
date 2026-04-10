package view;

import app.AppContext;
import facade.CriminalManagement;
import model.BiometricData;
import model.CaseRecord;
import model.Criminal;
import model.Evidence;
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
            System.out.println("1. Register Criminal");
            System.out.println("2. Search Criminal by ID");
            System.out.println("3. Update Criminal Crime Type");
            System.out.println("4. Delete Criminal");
            System.out.println("5. List All Criminals");
            System.out.println("6. View Criminal Details");
            System.out.println("7. Create Case");
            System.out.println("8. Assign Case");
            System.out.println("9. List All Cases");
            System.out.println("10. Add Evidence");
            System.out.println("11. List Evidence By Case");
            System.out.println("12. Store Biometric Data");
            System.out.println("13. View Biometric Data");
            System.out.println("14. Exit");
            System.out.print("Choose option: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1:
                    registerCriminalFlow(sc, management);
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
                    viewCriminalDetailsFlow(sc, management);
                    break;
                case 7:
                    createCaseFlow(sc, management);
                    break;
                case 8:
                    assignCaseFlow(sc, management);
                    break;
                case 9:
                    listAllCasesFlow(management);
                    break;
                case 10:
                    addEvidenceFlow(sc, management);
                    break;
                case 11:
                    listEvidenceByCaseFlow(sc, management);
                    break;
                case 12:
                    storeBiometricFlow(sc, management);
                    break;
                case 13:
                    viewBiometricFlow(sc, management);
                    break;
                case 14:
                    System.out.println("Exiting system. Goodbye.");
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1-14.");
            }
        }
    }

    private static void registerCriminalFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID: ");
        int id = readInt(sc);

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter Age: ");
        int age = readInt(sc);

        System.out.print("Enter Crime Type: ");
        String crime = sc.nextLine().trim();

        boolean added = management.registerCriminal(id, name, age, crime);
        if (added) {
            System.out.println("Criminal registered successfully.");
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

    private static void viewCriminalDetailsFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID to view details: ");
        int id = readInt(sc);
        Criminal criminal = management.getCriminalDetails(id);
        if (criminal == null) {
            System.out.println("No criminal found for ID " + id + ".");
            return;
        }
        printCriminal(criminal);
    }

    private static void createCaseFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Case ID: ");
        int caseId = readInt(sc);

        System.out.print("Enter Criminal ID: ");
        int criminalId = readInt(sc);

        System.out.print("Enter Case Description: ");
        String description = sc.nextLine().trim();

        System.out.print("Enter Assigned Officer (optional): ");
        String assignedOfficer = sc.nextLine().trim();

        boolean created;
        if (assignedOfficer.isEmpty()) {
            created = management.createCase(caseId, criminalId, description);
        } else {
            created = management.createCase(caseId, criminalId, description, assignedOfficer);
        }

        if (created) {
            System.out.println("Case created successfully.");
        } else {
            System.out.println("Case creation failed. Check Criminal ID or duplicate Case ID.");
        }
    }

    private static void assignCaseFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Case ID to assign: ");
        int caseId = readInt(sc);

        System.out.print("Enter Officer Name: ");
        String officer = sc.nextLine().trim();

        boolean assigned = management.assignCase(caseId, officer);
        if (assigned) {
            System.out.println("Case assigned successfully.");
        } else {
            System.out.println("Case ID not found.");
        }
    }

    private static void listAllCasesFlow(CriminalManagement management) {
        Collection<CaseRecord> cases = management.getAllCases();
        if (cases.isEmpty()) {
            System.out.println("No case records found.");
            return;
        }

        System.out.println("\n--- Case Records ---");
        for (CaseRecord caseRecord : cases) {
            System.out.println(
                "Case ID: " + caseRecord.getCaseId() +
                " | Criminal ID: " + caseRecord.getCriminalId() +
                " | Assigned Officer: " + caseRecord.getAssignedOfficer() +
                " | Description: " + caseRecord.getDescription()
            );
        }
    }

    private static void addEvidenceFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Evidence ID: ");
        int evidenceId = readInt(sc);

        System.out.print("Enter Case ID: ");
        int caseId = readInt(sc);

        System.out.print("Enter Evidence Type: ");
        String evidenceType = sc.nextLine().trim();

        boolean added = management.addEvidence(evidenceId, caseId, evidenceType);
        if (added) {
            System.out.println("Evidence added successfully.");
        } else {
            System.out.println("Evidence add failed. Check duplicate Evidence ID or Case ID.");
        }
    }

    private static void listEvidenceByCaseFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Case ID to list evidence: ");
        int caseId = readInt(sc);
        Collection<Evidence> evidenceList = management.getEvidenceByCase(caseId);
        if (evidenceList.isEmpty()) {
            System.out.println("No evidence found for case " + caseId + ".");
            return;
        }

        System.out.println("\n--- Evidence Records ---");
        for (Evidence evidence : evidenceList) {
            System.out.println(
                "Evidence ID: " + evidence.getEvidenceId() +
                " | Case ID: " + evidence.getCaseId() +
                " | Type: " + evidence.getEvidenceType()
            );
        }
    }

    private static void storeBiometricFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID: ");
        int criminalId = readInt(sc);

        System.out.print("Enter Fingerprint: ");
        String fingerprint = sc.nextLine().trim();

        System.out.print("Enter DNA: ");
        String dna = sc.nextLine().trim();

        boolean stored = management.storeBiometric(criminalId, fingerprint, dna);
        if (stored) {
            System.out.println("Biometric data stored successfully.");
        } else {
            System.out.println("Biometric storage failed. Criminal ID not found.");
        }
    }

    private static void viewBiometricFlow(Scanner sc, CriminalManagement management) {
        System.out.print("Enter Criminal ID to view biometric data: ");
        int criminalId = readInt(sc);
        BiometricData biometricData = management.getBiometric(criminalId);

        if (biometricData == null) {
            System.out.println("No biometric data found for criminal " + criminalId + ".");
            return;
        }

        System.out.println(
            "Criminal ID: " + biometricData.getCriminalId() +
            " | Fingerprint: " + biometricData.getFingerprint() +
            " | DNA: " + biometricData.getDna()
        );
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