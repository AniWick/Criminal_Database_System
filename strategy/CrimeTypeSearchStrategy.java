package strategy;

import model.Criminal;
import java.util.Collection;
import java.util.stream.Collectors;

public class CrimeTypeSearchStrategy implements AdvancedSearchStrategy {
    private String crimeType;

    public CrimeTypeSearchStrategy(String crimeType) {
        this.crimeType = crimeType.toLowerCase();
    }

    @Override
    public Collection<Criminal> search(Collection<Criminal> criminals) {
        return criminals.stream()
                .filter(c -> c.getCrimeType().toLowerCase().contains(crimeType))
                .collect(Collectors.toList());
    }
}
