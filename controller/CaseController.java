package controller;

import model.CaseRecord;
import model.ModelFactory;
import service.CaseService;
import java.util.Collection;

public class CaseController {

    private final CaseService service;

    public CaseController() {
        this(new CaseService());
    }

    public CaseController(CaseService service) {
        this.service = service;
    }

    public boolean createCase(int caseId, int criminalId, String desc) {
        CaseRecord caseRecord = ModelFactory.createCaseRecord(caseId, criminalId, desc);
        return service.createCase(caseRecord);
    }

    public boolean createCase(int caseId, int criminalId, String desc, String assignedOfficer) {
        CaseRecord caseRecord = ModelFactory.createCaseRecord(caseId, criminalId, desc, assignedOfficer);
        return service.createCase(caseRecord);
    }

    public boolean assignCase(int caseId, String assignedOfficer) {
        return service.assignCase(caseId, assignedOfficer);
    }

    public Collection<CaseRecord> getAllCases() {
        return service.getAllCases();
    }
}