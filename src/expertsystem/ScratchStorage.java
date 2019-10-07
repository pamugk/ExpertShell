package expertsystem;

import base.rules.Fact;
import base.rules.Rule;

import java.util.*;

public class ScratchStorage {
    private Map<UUID, Fact> usedFacts;
    private TreeMap<UUID, Rule> activatedRules;

    public ScratchStorage() {
        usedFacts = new HashMap<>();
        activatedRules = new TreeMap<>();
    }

    public void useFact(Fact fact) {
        usedFacts.put(fact.getVariable().getGuid(), fact);
    }

    public List<Fact> getUsedFacts() {
        return new ArrayList<>(usedFacts.values());
    }

    public TreeMap<UUID, Rule> getActivatedRules() {
        return activatedRules;
    }
}
