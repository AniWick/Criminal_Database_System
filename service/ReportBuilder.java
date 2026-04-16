package service;

import model.Report;
import model.Criminal;
import model.CaseRecord;
import model.Evidence;
import java.util.*;

public class ReportBuilder {
    private int reportId;
    private String title;
    private String type;
    private StringBuilder content;
    private String generatedBy;
    private Collection<Criminal> criminals;
    private Collection<CaseRecord> cases;
    private Collection<Evidence> evidences;

    public ReportBuilder(int reportId, String generatedBy) {
        this.reportId = reportId;
        this.generatedBy = generatedBy;
        this.content = new StringBuilder();
    }

    public ReportBuilder withCriminalStats(Collection<Criminal> criminals) {
        this.type = "CRIMINAL_STATS";
        this.title = "Criminal Statistics Report";
        this.criminals = criminals;
        buildCriminalStats();
        return this;
    }

    public ReportBuilder withCaseSummary(Collection<CaseRecord> cases) {
        this.type = "CASE_SUMMARY";
        this.title = "Case Summary Report";
        this.cases = cases;
        buildCaseSummary();
        return this;
    }

    public ReportBuilder withEvidenceInventory(Collection<Evidence> evidences) {
        this.type = "EVIDENCE_INVENTORY";
        this.title = "Evidence Inventory Report";
        this.evidences = evidences;
        buildEvidenceInventory();
        return this;
    }

    private void buildCriminalStats() {
        content.append("===== CRIMINAL STATISTICS REPORT =====\n");
        content.append("Total Criminals: ").append(criminals.size()).append("\n\n");
        
        Map<String, Long> crimeCount = new HashMap<>();
        for (Criminal c : criminals) {
            crimeCount.put(c.getCrimeType(), crimeCount.getOrDefault(c.getCrimeType(), 0L) + 1);
        }
        
        content.append("Crime Type Breakdown:\n");
        crimeCount.forEach((crime, count) -> 
            content.append("  - ").append(crime).append(": ").append(count).append(" criminals\n")
        );
        
        double avgAge = criminals.stream().mapToInt(Criminal::getAge).average().orElse(0);
        content.append("\nAverage Age: ").append(String.format("%.2f", avgAge)).append("\n");
    }

    private void buildCaseSummary() {
        content.append("===== CASE SUMMARY REPORT =====\n");
        content.append("Total Cases: ").append(cases.size()).append("\n\n");
        
        long assignedCases = cases.stream()
                .filter(c -> c.getAssignedOfficer() != null && !c.getAssignedOfficer().isEmpty())
                .count();
        long unassignedCases = cases.size() - assignedCases;
        
        content.append("Assigned Cases: ").append(assignedCases).append("\n");
        content.append("Unassigned Cases: ").append(unassignedCases).append("\n\n");
        
        content.append("Case Details:\n");
        for (CaseRecord c : cases) {
            content.append("  Case ID ").append(c.getCaseId())
                    .append(": Criminal ").append(c.getCriminalId())
                    .append(" - ")
                    .append(c.getDescription())
                    .append(" [Assigned to: ")
                    .append(c.getAssignedOfficer() != null ? c.getAssignedOfficer() : "Unassigned")
                    .append("]\n");
        }
    }

    private void buildEvidenceInventory() {
        content.append("===== EVIDENCE INVENTORY REPORT =====\n");
        content.append("Total Evidence Items: ").append(evidences.size()).append("\n\n");
        
        Map<String, Long> typeCount = new HashMap<>();
        for (Evidence e : evidences) {
            typeCount.put(e.getEvidenceType(), typeCount.getOrDefault(e.getEvidenceType(), 0L) + 1);
        }
        
        content.append("Evidence Type Distribution:\n");
        typeCount.forEach((type, count) ->
            content.append("  - ").append(type).append(": ").append(count).append(" items\n")
        );
    }

    public Report build() {
        return new Report(reportId, title, type, content.toString(), generatedBy);
    }
}
