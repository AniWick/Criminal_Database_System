package facade;

import model.BiometricData;
import model.CaseRecord;
import model.Criminal;
import model.Evidence;
import java.util.Collection;

public interface CriminalManagement {
    boolean login(String username, String password);
    boolean registerCriminal(int id, String name, int age, String crime);
    boolean addCriminal(int id, String name, int age, String crime);
    Criminal searchCriminal(int id);
    Criminal getCriminalDetails(int id);
    boolean updateCrime(int id, String crime);
    boolean deleteCriminal(int id);
    Collection<Criminal> getAllCriminals();
    boolean createCase(int caseId, int criminalId, String description);
    boolean createCase(int caseId, int criminalId, String description, String assignedOfficer);
    boolean assignCase(int caseId, String assignedOfficer);
    Collection<CaseRecord> getAllCases();
    boolean addEvidence(int evidenceId, int caseId, String evidenceType);
    Collection<Evidence> getEvidenceByCase(int caseId);
    boolean storeBiometric(int criminalId, String fingerprint, String dna);
    BiometricData getBiometric(int criminalId);
}