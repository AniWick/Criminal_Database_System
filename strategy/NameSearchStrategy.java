package strategy;

import model.Criminal;
import java.util.Collection;
import java.util.stream.Collectors;

public class NameSearchStrategy implements AdvancedSearchStrategy {
    private String name;

    public NameSearchStrategy(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public Collection<Criminal> search(Collection<Criminal> criminals) {
        return criminals.stream()
                .filter(c -> c.getName().toLowerCase().contains(name))
                .collect(Collectors.toList());
    }
}
