package validation;

public final class CriminalValidator {

    private CriminalValidator() {
    }

    public static boolean isValid(int id, String name, int age, String crime) {
        return id > 0
            && name != null && !name.trim().isEmpty()
            && age > 0 && age < 120
            && crime != null && !crime.trim().isEmpty();
    }
}