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

    private static class DatabaseSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private Map<Integer, Criminal> criminals;
        private Map<Integer, CaseRecord> cases;
        private Map<Integer, Evidence> evidences;
        private Map<Integer, BiometricData> biometrics;
        private Map<String, User> users;
    }
}