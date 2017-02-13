package doubleEntryComponents;

import doubleEntryComponents.actions.Action;
import doubleEntryComponents.contracts.Contract;

import java.util.ArrayList;

public abstract class Agent {
    private String name;

    public Agent(String name) {
        this.name = name;
    }
    public abstract void add(Contract contract);
    public abstract void addCash(double amount);
    public abstract ArrayList<Action> getAvailableActions(Agent me);

    public String getName() {
        return name;
    }



}
