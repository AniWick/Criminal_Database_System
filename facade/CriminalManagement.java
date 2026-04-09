package facade;

import model.Criminal;
import java.util.Collection;

public interface CriminalManagement {
    boolean login(String username, String password);
    boolean addCriminal(int id, String name, int age, String crime);
    Criminal searchCriminal(int id);
    boolean updateCrime(int id, String crime);
    boolean deleteCriminal(int id);
    Collection<Criminal> getAllCriminals();
}