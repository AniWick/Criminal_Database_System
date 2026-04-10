package controller;

import model.BiometricData;
import model.ModelFactory;
import service.BiometricService;

public class BiometricController {

    private final BiometricService service;

    public BiometricController() {
        this(new BiometricService());
    }

    public BiometricController(BiometricService service) {
        this.service = service;
    }

    public boolean storeBiometric(int criminalId, String fingerprint, String dna) {
        BiometricData data = ModelFactory.createBiometricData(criminalId, fingerprint, dna);
        return service.storeBiometric(data);
    }

    public BiometricData getBiometric(int criminalId) {
        return service.getBiometric(criminalId);
    }
}