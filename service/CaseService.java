package service;

import database.CriminalDatabase;
import model.CaseRecord;
import java.util.*;

public class CaseService {

    public boolean createCase(CaseRecord caseRecord) {
        return CriminalDatabase.createCase(caseRecord);
    }

    public Collection<CaseRecord> getAllCases() {
        return CriminalDatabase.getAllCases();
    }

    public boolean assignCase(int caseId, String assignedOfficer) {
        return CriminalDatabase.assignCase(caseId, assignedOfficer);
    }
}