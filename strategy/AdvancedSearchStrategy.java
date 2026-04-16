package strategy;

import model.Criminal;
import java.util.Collection;

public interface AdvancedSearchStrategy {
    Collection<Criminal> search(Collection<Criminal> criminals);
}
