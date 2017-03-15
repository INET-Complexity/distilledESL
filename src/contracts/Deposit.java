package contracts;

import actions.Action;
import agents.Agent;

import java.util.Collections;
import java.util.List;

public class Deposit extends Contract {

    private Agent depositor;
    private Agent holder;
    private double amount;

    public Deposit(Agent depositor, Agent holder, double amount) {
        this.depositor = depositor;
        this.holder = holder;
        this.amount = amount;
    }

    @Override
    public Agent getAssetParty() {
        return depositor;
    }

    @Override
    public Agent getLiabilityParty() {
        return holder;
    }

    public double getValue() {
        return amount;
    }

    @Override
    public List<Action> getAvailableActions(Agent me) {
        return Collections.emptyList();
    }
}
