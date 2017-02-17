package agents;

import actions.Action;
import contracts.Contract;

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

    public abstract void raiseLiquidity(double amount);
    public abstract void pullFunding(double amount);
    public abstract void payLoan(double amount) throws Exception;

}
