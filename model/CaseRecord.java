package model;

import java.io.Serializable;

public class CaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private int caseId;
    private int criminalId;
    private String description;
    private String assignedOfficer;

    public CaseRecord(int caseId, int criminalId, String description) {
        this(caseId, criminalId, description, "Unassigned");
    }

    public CaseRecord(int caseId, int criminalId, String description, String assignedOfficer) {
        this.caseId = caseId;
        this.criminalId = criminalId;
        this.description = description;
        this.assignedOfficer = assignedOfficer;
    }

    public int getCaseId() { return caseId; }
    public int getCriminalId() { return criminalId; }
    public String getDescription() { return description; }
    public String getAssignedOfficer() {
        if (assignedOfficer == null || assignedOfficer.trim().isEmpty()) {
            return "Unassigned";
        }
        return assignedOfficer;
    }

    public void setAssignedOfficer(String assignedOfficer) {
        this.assignedOfficer = assignedOfficer;
    }
}