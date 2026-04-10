package controller;

import model.Evidence;
import model.ModelFactory;
import service.EvidenceService;
import java.util.Collection;

public class EvidenceController {

    private final EvidenceService service;

    public EvidenceController() {
        this(new EvidenceService());
    }

    public EvidenceController(EvidenceService service) {
        this.service = service;
    }

    public boolean addEvidence(int evidenceId, int caseId, String evidenceType) {
        Evidence evidence = ModelFactory.createEvidence(evidenceId, caseId, evidenceType);
        return service.addEvidence(evidence);
    }

    public Collection<Evidence> getEvidenceByCase(int caseId) {
        return service.getEvidenceByCase(caseId);
    }
}