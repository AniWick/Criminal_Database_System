package model;

import java.io.Serializable;

public class BiometricData implements Serializable {

    private static final long serialVersionUID = 1L;

    private int criminalId;
    private String fingerprint;
    private String dna;

    public BiometricData(int criminalId, String fingerprint, String dna) {
        this.criminalId = criminalId;
        this.fingerprint = fingerprint;
        this.dna = dna;
    }

    public int getCriminalId() { return criminalId; }
    public String getFingerprint() { return fingerprint; }
    public String getDna() { return dna; }
}