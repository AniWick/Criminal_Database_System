package model;

import java.io.Serializable;

public class CaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private int caseId;
    private int criminalId;
    private String description;

    public CaseRecord(int caseId, int criminalId, String description) {
        this.caseId = caseId;
        this.criminalId = criminalId;
        this.description = description;
    }

    public int getCaseId() { return caseId; }
    public int getCriminalId() { return criminalId; }
    public String getDescription() { return description; }
}