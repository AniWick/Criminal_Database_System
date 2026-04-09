package service;

import model.Criminal;
import strategy.CriminalIdSearchStrategy;
import strategy.CriminalSearchStrategy;
import repository.CriminalRepository;
import repository.InMemoryCriminalRepository;
import java.util.*;

public class CriminalService {

    private final CriminalSearchStrategy searchStrategy;
    private final CriminalRepository repository;

    public CriminalService() {
        this(new InMemoryCriminalRepository());
    }

    public CriminalService(CriminalRepository repository) {
        this(new CriminalIdSearchStrategy(repository), repository);
    }

    public CriminalService(CriminalSearchStrategy searchStrategy, CriminalRepository repository) {
        this.searchStrategy = searchStrategy;
        this.repository = repository;
    }

    public boolean addCriminal(Criminal criminal) {
        return repository.addCriminal(criminal);
    }

    public Criminal searchCriminal(int id) {
        return searchStrategy.search(id);
    }

    public boolean updateCrime(int id, String crime) {
        return repository.updateCrime(id, crime);
    }

    public boolean deleteCriminal(int id) {
        return repository.deleteCriminal(id);
    }

    public Collection<Criminal> getAllCriminals() {
        return repository.getAllCriminals();
    }
}