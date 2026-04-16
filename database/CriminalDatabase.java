package database;

import model.*;
import java.io.*;
import java.util.*;

public class CriminalDatabase {

    private static final String DATA_DIR = "data";
    private static final String DB_FILE = DATA_DIR + File.separator + "criminal-management-db.ser";

    private static Map<Integer, Criminal> criminals = new LinkedHashMap<>();
    private static Map<Integer, CaseRecord> cases = new LinkedHashMap<>();
    private static Map<Integer, Evidence> evidences = new LinkedHashMap<>();
    private static Map<Integer, BiometricData> biometrics = new LinkedHashMap<>();
    private static Map<String, User> users = new LinkedHashMap<>();

    static {
        loadFromDisk();
        ensureMinimumSeedData();
    }

    public static synchronized User getUser(String username) {
        return users.get(username);
    }

    public static synchronized boolean addCriminal(Criminal criminal) {
        if (criminals.containsKey(criminal.getCriminalId())) {
            return false;
        }
        criminals.put(criminal.getCriminalId(), criminal);
        saveToDisk();
        return true;
    }

    public static synchronized Criminal getCriminal(int id) {
        return criminals.get(id);
    }

    public static synchronized boolean updateCrime(int id, String crime) {
        Criminal c = criminals.get(id);
        if (c == null) {
            return false;
        }
        c.setCrimeType(crime);
        saveToDisk();
        return true;
    }

    public static synchronized boolean deleteCriminal(int id) {
        Criminal removed = criminals.remove(id);
        if (removed == null) {
            return false;
        }

        // Basic relational cleanup: remove dependent records.
        biometrics.remove(id);

        Set<Integer> deletedCaseIds = new HashSet<>();
        Iterator<Map.Entry<Integer, CaseRecord>> caseIterator = cases.entrySet().iterator();
        while (caseIterator.hasNext()) {
            Map.Entry<Integer, CaseRecord> entry = caseIterator.next();
            if (entry.getValue().getCriminalId() == id) {
                deletedCaseIds.add(entry.getKey());
                caseIterator.remove();
            }
        }

        Iterator<Map.Entry<Integer, Evidence>> evidenceIterator = evidences.entrySet().iterator();
        while (evidenceIterator.hasNext()) {
            Map.Entry<Integer, Evidence> entry = evidenceIterator.next();
            if (deletedCaseIds.contains(entry.getValue().getCaseId())) {
                evidenceIterator.remove();
            }
        }

        saveToDisk();
        return true;
    }

    public static synchronized Collection<Criminal> getAllCriminals() {
        return new ArrayList<>(criminals.values());
    }

    public static synchronized boolean createCase(CaseRecord caseRecord) {
        if (!criminals.containsKey(caseRecord.getCriminalId()) || cases.containsKey(caseRecord.getCaseId())) {
            return false;
        }
        cases.put(caseRecord.getCaseId(), caseRecord);
        saveToDisk();
        return true;
    }

    public static synchronized Collection<CaseRecord> getAllCases() {
        return new ArrayList<>(cases.values());
    }

    public static synchronized boolean assignCase(int caseId, String assignedOfficer) {
        CaseRecord caseRecord = cases.get(caseId);
        if (caseRecord == null) {
            return false;
        }
        caseRecord.setAssignedOfficer(assignedOfficer);
        saveToDisk();
        return true;
    }

    public static synchronized boolean addEvidence(Evidence evidence) {
        if (evidence == null) {
            return false;
        }
        if (evidences.containsKey(evidence.getEvidenceId()) || !cases.containsKey(evidence.getCaseId())) {
            return false;
        }
        evidences.put(evidence.getEvidenceId(), evidence);
        saveToDisk();
        return true;
    }

    public static synchronized Collection<Evidence> getEvidenceByCase(int caseId) {
        List<Evidence> byCase = new ArrayList<>();
        for (Evidence evidence : evidences.values()) {
            if (evidence.getCaseId() == caseId) {
                byCase.add(evidence);
            }
        }
        return byCase;
    }

    public static synchronized boolean storeBiometric(BiometricData biometricData) {
        if (biometricData == null || !criminals.containsKey(biometricData.getCriminalId())) {
            return false;
        }
        biometrics.put(biometricData.getCriminalId(), biometricData);
        saveToDisk();
        return true;
    }

    public static synchronized BiometricData getBiometric(int criminalId) {
        return biometrics.get(criminalId);
    }

    @SuppressWarnings("unchecked")
    private static void loadFromDisk() {
        File dataFile = new File(DB_FILE);
        if (!dataFile.exists()) {
            seedDefaults();
            saveToDisk();
            return;
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(dataFile))) {
            DatabaseSnapshot snapshot = (DatabaseSnapshot) input.readObject();
            criminals = new LinkedHashMap<>(snapshot.criminals);
            cases = new LinkedHashMap<>(snapshot.cases);
            evidences = new LinkedHashMap<>(snapshot.evidences);
            biometrics = new LinkedHashMap<>(snapshot.biometrics);
            users = new LinkedHashMap<>(snapshot.users);
        } catch (IOException | ClassNotFoundException e) {
            seedDefaults();
            saveToDisk();
        }
    }

    private static void saveToDisk() {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            DatabaseSnapshot snapshot = new DatabaseSnapshot();
            snapshot.criminals = criminals;
            snapshot.cases = cases;
            snapshot.evidences = evidences;
            snapshot.biometrics = biometrics;
            snapshot.users = users;

            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
                output.writeObject(snapshot);
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to persist database: " + e.getMessage());
        }
    }

    private static void seedDefaults() {
        users = new LinkedHashMap<>();
        users.put("admin", ModelFactory.createUser("admin", "admin123", "Officer"));
        users.put("detective", ModelFactory.createUser("detective", "detect123", "Detective"));

        criminals = new LinkedHashMap<>();
        criminals.put(1, ModelFactory.createCriminal(1, "John Doe", 32, "Robbery"));
        criminals.put(2, ModelFactory.createCriminal(2, "Alex Mercer", 40, "Cyber Crime"));
        criminals.put(3, ModelFactory.createCriminal(3, "Maria Lopez", 29, "Drug Trafficking"));
        criminals.put(4, ModelFactory.createCriminal(4, "David King", 36, "Fraud"));

        cases = new LinkedHashMap<>();
        cases.put(101, ModelFactory.createCaseRecord(101, 1, "Bank robbery investigation"));
        cases.put(102, ModelFactory.createCaseRecord(102, 2, "International hacking case"));
        cases.put(103, ModelFactory.createCaseRecord(103, 3, "Drug smuggling ring"));
        cases.put(104, ModelFactory.createCaseRecord(104, 4, "Financial fraud case"));

        evidences = new LinkedHashMap<>();
        evidences.put(1, ModelFactory.createEvidence(1, 101, "CCTV Footage"));
        evidences.put(2, ModelFactory.createEvidence(2, 102, "Laptop"));
        evidences.put(3, ModelFactory.createEvidence(3, 103, "Drug Samples"));
        evidences.put(4, ModelFactory.createEvidence(4, 104, "Bank Records"));

        biometrics = new LinkedHashMap<>();
        biometrics.put(1, ModelFactory.createBiometricData(1, "FP12345", "DNA67890"));
        biometrics.put(2, ModelFactory.createBiometricData(2, "FP54321", "DNA09876"));
        biometrics.put(3, ModelFactory.createBiometricData(3, "FP11111", "DNA22222"));
        biometrics.put(4, ModelFactory.createBiometricData(4, "FP33333", "DNA44444"));
    }

    private static void ensureMinimumSeedData() {
        boolean changed = false;

        if (!users.containsKey("admin")) {
            users.put("admin", ModelFactory.createUser("admin", "admin123", "Officer"));
            changed = true;
        }
        if (!users.containsKey("detective")) {
            users.put("detective", ModelFactory.createUser("detective", "detect123", "Detective"));
            changed = true;
        }

        Object[][] demoRows = new Object[][] {
            {1001, "Ethan Cole", 31, "Armed Robbery", 2101, "Downtown vault breach", "Officer Cruz", 5101, "CCTV Footage", "FP-E1001", "DNA-E1001"},
            {1002, "Nadia Voss", 27, "Identity Theft", 2102, "Synthetic identity network", "Officer Reed", 5102, "Forged IDs", "FP-N1002", "DNA-N1002"},
            {1003, "Carlos Mendez", 39, "Drug Trafficking", 2103, "Interstate narcotics route", "Detective Park", 5103, "Chemical Samples", "FP-C1003", "DNA-C1003"},
            {1004, "Liam Porter", 44, "Fraud", 2104, "Insurance fraud ring", "Officer Ellis", 5104, "Financial Ledger", "FP-L1004", "DNA-L1004"},
            {1005, "Ivy Sloan", 35, "Cyber Crime", 2105, "Ransomware extortion", "Detective Vega", 5105, "Encrypted Drive", "FP-I1005", "DNA-I1005"},
            {1006, "Marcus Flint", 29, "Kidnapping", 2106, "Hostage relocation case", "Officer Hale", 5106, "Vehicle Fibers", "FP-M1006", "DNA-M1006"},
            {1007, "Selena Hart", 33, "Money Laundering", 2107, "Layered shell-company transfers", "Detective Khan", 5107, "Bank Statements", "FP-S1007", "DNA-S1007"},
            {1008, "Noah Briggs", 41, "Arson", 2108, "Warehouse ignition incident", "Officer Bryant", 5108, "Fuel Residue", "FP-N1008", "DNA-N1008"},
            {1009, "Priya Nair", 30, "Extortion", 2109, "Construction racket threats", "Officer Morgan", 5109, "Audio Recording", "FP-P1009", "DNA-P1009"},
            {1010, "Omar Haddad", 37, "Weapons Smuggling", 2110, "Unlicensed arms movement", "Detective Singh", 5110, "Ballistic Fragments", "FP-O1010", "DNA-O1010"}
        };

        for (Object[] row : demoRows) {
            int criminalId = (Integer) row[0];
            String name = (String) row[1];
            int age = (Integer) row[2];
            String crimeType = (String) row[3];
            int caseId = (Integer) row[4];
            String caseDesc = (String) row[5];
            String officer = (String) row[6];
            int evidenceId = (Integer) row[7];
            String evidenceType = (String) row[8];
            String fingerprint = (String) row[9];
            String dna = (String) row[10];

            if (!criminals.containsKey(criminalId)) {
                criminals.put(criminalId, ModelFactory.createCriminal(criminalId, name, age, crimeType));
                changed = true;
            }

            if (!cases.containsKey(caseId) && criminals.containsKey(criminalId)) {
                cases.put(caseId, ModelFactory.createCaseRecord(caseId, criminalId, caseDesc, officer));
                changed = true;
            }

            if (!evidences.containsKey(evidenceId) && cases.containsKey(caseId)) {
                evidences.put(evidenceId, ModelFactory.createEvidence(evidenceId, caseId, evidenceType));
                changed = true;
            }

            if (!biometrics.containsKey(criminalId) && criminals.containsKey(criminalId)) {
                biometrics.put(criminalId, ModelFactory.createBiometricData(criminalId, fingerprint, dna));
                changed = true;
            }
        }

        for (Criminal criminal : criminals.values()) {
            int criminalId = criminal.getCriminalId();
            if (!biometrics.containsKey(criminalId)) {
                biometrics.put(
                    criminalId,
                    ModelFactory.createBiometricData(criminalId, "FP-AUTO-" + criminalId, "DNA-AUTO-" + criminalId)
                );
                changed = true;
            }
        }

        if (changed) {
            saveToDisk();
        }
    }

    private static class DatabaseSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private Map<Integer, Criminal> criminals;
        private Map<Integer, CaseRecord> cases;
        private Map<Integer, Evidence> evidences;
        private Map<Integer, BiometricData> biometrics;
        private Map<String, User> users;
    }
}