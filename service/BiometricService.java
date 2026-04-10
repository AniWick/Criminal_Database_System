package service;

import database.CriminalDatabase;
import model.BiometricData;

public class BiometricService {

    public boolean storeBiometric(BiometricData data) {
        return CriminalDatabase.storeBiometric(data);
    }

    public BiometricData getBiometric(int criminalId) {
        return CriminalDatabase.getBiometric(criminalId);
    }
}