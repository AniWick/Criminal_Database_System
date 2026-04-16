package strategy;

import model.Criminal;
import java.util.Collection;
import java.util.stream.Collectors;

public class AgeRangeSearchStrategy implements AdvancedSearchStrategy {
    private int minAge;
    private int maxAge;

    public AgeRangeSearchStrategy(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    @Override
    public Collection<Criminal> search(Collection<Criminal> criminals) {
        return criminals.stream()
                .filter(c -> c.getAge() >= minAge && c.getAge() <= maxAge)
                .collect(Collectors.toList());
    }
}
