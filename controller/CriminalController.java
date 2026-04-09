package controller;

import model.Criminal;
import service.CriminalService;
import validation.CriminalValidator;
import java.util.Collection;

public class CriminalController {

    private final CriminalService service;

    public CriminalController() {
        this(new CriminalService());
    }

    public CriminalController(CriminalService service) {
        this.service = service;
    }

    public boolean addCriminal(int id, String name, int age, String crime) {
        if (!CriminalValidator.isValid(id, name, age, crime)) {
            return false;
        }

        Criminal criminal = new Criminal.Builder()
            .criminalId(id)
            .name(name)
            .age(age)
            .crimeType(crime)
            .build();
        return service.addCriminal(criminal);
    }

    public Criminal searchCriminal(int id) {
        return service.searchCriminal(id);
    }

    public boolean updateCrime(int id, String crime) {
        return service.updateCrime(id, crime);
    }

    public boolean deleteCriminal(int id) {
        return service.deleteCriminal(id);
    }

    public Collection<Criminal> getAllCriminals() {
        return service.getAllCriminals();
    }
}