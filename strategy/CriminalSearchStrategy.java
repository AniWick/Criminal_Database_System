package strategy;

import model.Criminal;

public interface CriminalSearchStrategy {
    Criminal search(int id);
}