package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    private int reportId;
    private String title;
    private String type; // CRIMINAL_STATS, CASE_SUMMARY, EVIDENCE_INVENTORY, OFFICER_PERFORMANCE
    private String content;
    private LocalDateTime generatedAt;
    private String generatedBy;

    public Report(int reportId, String title, String type, String content, String generatedBy) {
        this.reportId = reportId;
        this.title = title;
        this.type = type;
        this.content = content;
        this.generatedBy = generatedBy;
        this.generatedAt = LocalDateTime.now();
    }

    public int getReportId() { return reportId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public String getGeneratedBy() { return generatedBy; }
}
