package repository;

import model.Criminal;
import java.util.Collection;

public interface CriminalRepository {
    boolean addCriminal(Criminal criminal);
    Criminal getCriminal(int id);
    boolean updateCrime(int id, String crime);
    boolean deleteCriminal(int id);
    Collection<Criminal> getAllCriminals();
}