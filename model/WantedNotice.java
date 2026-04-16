package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class WantedNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    private int criminalId;
    private int wantedId;
    private String reason;
    private String severity; // HIGH, MEDIUM, LOW
    private LocalDateTime dateAdded;
    private boolean active;

    public WantedNotice(int wantedId, int criminalId, String reason, String severity) {
        this.wantedId = wantedId;
        this.criminalId = criminalId;
        this.reason = reason;
        this.severity = severity;
        this.dateAdded = LocalDateTime.now();
        this.active = true;
    }

    public int getWantedId() { return wantedId; }
    public int getCriminalId() { return criminalId; }
    public String getReason() { return reason; }
    public String getSeverity() { return severity; }
    public LocalDateTime getDateAdded() { return dateAdded; }
    public boolean isActive() { return active; }

    public void deactivate() {
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }
}
