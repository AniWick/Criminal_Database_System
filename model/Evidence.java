package model;

import java.io.Serializable;

public class Evidence implements Serializable {

    private static final long serialVersionUID = 1L;

    private int evidenceId;
    private int caseId;
    private String evidenceType;

    public Evidence(int evidenceId, int caseId, String evidenceType) {
        this.evidenceId = evidenceId;
        this.caseId = caseId;
        this.evidenceType = evidenceType;
    }

    public int getEvidenceId() { return evidenceId; }
    public int getCaseId() { return caseId; }
    public String getEvidenceType() { return evidenceType; }
}