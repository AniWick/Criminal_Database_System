package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChainOfCustody implements Serializable {

    private static final long serialVersionUID = 1L;

    private int evidenceId;
    private int custodyId;
    private String handledBy;
    private String action; // COLLECTED, TRANSFERRED, STORED, TESTED
    private LocalDateTime timestamp;
    private String notes;
    private String location;

    public ChainOfCustody(int custodyId, int evidenceId, String handledBy, String action, String location, String notes) {
        this.custodyId = custodyId;
        this.evidenceId = evidenceId;
        this.handledBy = handledBy;
        this.action = action;
        this.location = location;
        this.notes = notes;
        this.timestamp = LocalDateTime.now();
    }

    public int getCustodyId() { return custodyId; }
    public int getEvidenceId() { return evidenceId; }
    public String getHandledBy() { return handledBy; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNotes() { return notes; }
    public String getLocation() { return location; }
}
