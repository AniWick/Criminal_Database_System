package strategy;

import model.Criminal;
import repository.CriminalRepository;

public class CriminalIdSearchStrategy implements CriminalSearchStrategy {

    private final CriminalRepository repository;

    public CriminalIdSearchStrategy(CriminalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Criminal search(int id) {
        return repository.getCriminal(id);
    }
}