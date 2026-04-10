package service;

import database.CriminalDatabase;
import model.Evidence;
import java.util.Collection;

public class EvidenceService {

    public boolean addEvidence(Evidence evidence) {
        return CriminalDatabase.addEvidence(evidence);
    }

    public Collection<Evidence> getEvidenceByCase(int caseId) {
        return CriminalDatabase.getEvidenceByCase(caseId);
    }
}