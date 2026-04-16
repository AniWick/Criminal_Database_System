package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private int notificationId;
    private String title;
    private String message;
    private String type; // CASE_ASSIGNED, EVIDENCE_UPDATED, ALERT, SYSTEM
    private String recipient;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification(int notificationId, String title, String message, String type, String recipient) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.recipient = recipient;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public int getNotificationId() { return notificationId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getRecipient() { return recipient; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() { return read; }

    public void markAsRead() {
        this.read = true;
    }
}
