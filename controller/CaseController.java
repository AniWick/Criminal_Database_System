package controller;

import model.CaseRecord;
import model.ModelFactory;
import service.CaseService;

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
}