package controller;

import model.Evidence;

public class EvidenceController {

    public void addEvidence(Evidence evidence) {
        System.out.println("Evidence added: " + evidence.getEvidenceType());
    }
}