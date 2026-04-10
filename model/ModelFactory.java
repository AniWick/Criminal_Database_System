package model;

public final class ModelFactory {

    private ModelFactory() {
    }

    public static Criminal createCriminal(int criminalId, String name, int age, String crimeType) {
        return new Criminal(criminalId, name, age, crimeType);
    }

    public static CaseRecord createCaseRecord(int caseId, int criminalId, String description) {
        return new CaseRecord(caseId, criminalId, description);
    }

    public static CaseRecord createCaseRecord(int caseId, int criminalId, String description, String assignedOfficer) {
        return new CaseRecord(caseId, criminalId, description, assignedOfficer);
    }

    public static Evidence createEvidence(int evidenceId, int caseId, String evidenceType) {
        return new Evidence(evidenceId, caseId, evidenceType);
    }

    public static BiometricData createBiometricData(int criminalId, String fingerprint, String dna) {
        return new BiometricData(criminalId, fingerprint, dna);
    }

    public static User createUser(String username, String password, String role) {
        return new User(username, password, role);
    }
}