package service;

import database.CriminalDatabase;
import model.BiometricData;

public class BiometricService {

    public void storeBiometric(BiometricData data) {
        CriminalDatabase.storeBiometric(data);
    }

    public BiometricData getBiometric(int criminalId) {
        return CriminalDatabase.getBiometric(criminalId);
    }
}