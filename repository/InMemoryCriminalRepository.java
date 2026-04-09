package repository;

import database.CriminalDatabase;
import model.Criminal;
import java.util.Collection;

public class InMemoryCriminalRepository implements CriminalRepository {

    @Override
    public boolean addCriminal(Criminal criminal) {
        return CriminalDatabase.addCriminal(criminal);
    }

    @Override
    public Criminal getCriminal(int id) {
        return CriminalDatabase.getCriminal(id);
    }

    @Override
    public boolean updateCrime(int id, String crime) {
        return CriminalDatabase.updateCrime(id, crime);
    }

    @Override
    public boolean deleteCriminal(int id) {
        return CriminalDatabase.deleteCriminal(id);
    }

    @Override
    public Collection<Criminal> getAllCriminals() {
        return CriminalDatabase.getAllCriminals();
    }
}