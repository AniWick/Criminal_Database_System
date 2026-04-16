package service;

import model.ChainOfCustody;
import java.util.*;
import java.util.stream.Collectors;

public class EvidenceChainService {
    private List<ChainOfCustody> chainRecords = Collections.synchronizedList(new ArrayList<>());
    private int custodyIdCounter = 5000;

    public boolean addCustodyRecord(int evidenceId, String handledBy, String action, String location, String notes) {
        ChainOfCustody record = new ChainOfCustody(custodyIdCounter++, evidenceId, handledBy, action, location, notes);
        return chainRecords.add(record);
    }

    public List<ChainOfCustody> getChainByEvidence(int evidenceId) {
        return new ArrayList<>(chainRecords.stream()
                .filter(c -> c.getEvidenceId() == evidenceId)
                .collect(Collectors.toList()));
    }

    public List<ChainOfCustody> getChainByHandler(String handledBy) {
        return new ArrayList<>(chainRecords.stream()
                .filter(c -> c.getHandledBy().equalsIgnoreCase(handledBy))
                .collect(Collectors.toList()));
    }

    public List<ChainOfCustody> getAllRecords() {
        return new ArrayList<>(chainRecords);
    }

    public void printChainReport(int evidenceId) {
        List<ChainOfCustody> chain = getChainByEvidence(evidenceId);
        System.out.println("\n=== Chain of Custody for Evidence ID " + evidenceId + " ===");
        for (ChainOfCustody c : chain) {
            System.out.println("  [" + c.getTimestamp() + "] " + c.getAction() + 
                    " by " + c.getHandledBy() + " at " + c.getLocation() + 
                    " - Notes: " + c.getNotes());
        }
    }
}
