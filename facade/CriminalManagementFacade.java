package facade;

import controller.BiometricController;
import controller.CaseController;
import controller.CriminalController;
import controller.EvidenceController;
import controller.LoginController;
import model.BiometricData;
import model.CaseRecord;
import model.Criminal;
import model.Evidence;
import java.util.Collection;

public class CriminalManagementFacade implements CriminalManagement {

    private final LoginController loginController;
    private final CriminalController criminalController;
    private final CaseController caseController;
    private final EvidenceController evidenceController;
    private final BiometricController biometricController;

    public CriminalManagementFacade() {
        this(new LoginController(), new CriminalController(), new CaseController(), new EvidenceController(), new BiometricController());
    }

    public CriminalManagementFacade(
        LoginController loginController,
        CriminalController criminalController,
        CaseController caseController,
        EvidenceController evidenceController,
        BiometricController biometricController
    ) {
        this.loginController = loginController;
        this.criminalController = criminalController;
        this.caseController = caseController;
        this.evidenceController = evidenceController;
        this.biometricController = biometricController;
    }

    @Override
    public boolean login(String username, String password) {
        return loginController.login(username, password);
    }

    @Override
    public boolean registerCriminal(int id, String name, int age, String crime) {
        return addCriminal(id, name, age, crime);
    }

    @Override
    public boolean addCriminal(int id, String name, int age, String crime) {
        return criminalController.addCriminal(id, name, age, crime);
    }

    @Override
    public Criminal searchCriminal(int id) {
        return criminalController.searchCriminal(id);
    }

    @Override
    public Criminal getCriminalDetails(int id) {
        return criminalController.searchCriminal(id);
    }

    @Override
    public boolean updateCrime(int id, String crime) {
        return criminalController.updateCrime(id, crime);
    }

    @Override
    public boolean deleteCriminal(int id) {
        return criminalController.deleteCriminal(id);
    }

    @Override
    public Collection<Criminal> getAllCriminals() {
        return criminalController.getAllCriminals();
    }

    @Override
    public boolean createCase(int caseId, int criminalId, String description) {
        return caseController.createCase(caseId, criminalId, description);
    }

    @Override
    public boolean createCase(int caseId, int criminalId, String description, String assignedOfficer) {
        return caseController.createCase(caseId, criminalId, description, assignedOfficer);
    }

    @Override
    public boolean assignCase(int caseId, String assignedOfficer) {
        return caseController.assignCase(caseId, assignedOfficer);
    }

    @Override
    public Collection<CaseRecord> getAllCases() {
        return caseController.getAllCases();
    }

    @Override
    public boolean addEvidence(int evidenceId, int caseId, String evidenceType) {
        return evidenceController.addEvidence(evidenceId, caseId, evidenceType);
    }

    @Override
    public Collection<Evidence> getEvidenceByCase(int caseId) {
        return evidenceController.getEvidenceByCase(caseId);
    }

    @Override
    public boolean storeBiometric(int criminalId, String fingerprint, String dna) {
        return biometricController.storeBiometric(criminalId, fingerprint, dna);
    }

    @Override
    public BiometricData getBiometric(int criminalId) {
        return biometricController.getBiometric(criminalId);
    }
}