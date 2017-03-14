package contracts;

import actions.Action;
import agents.Agent;

import java.util.ArrayList;

public class LongTermUnsecured extends Contract {

    private Agent liabilityParty;
    private double amount;

    public LongTermUnsecured(Agent liabilityParty, double amount) {
        this.amount = amount;
        this.liabilityParty = liabilityParty;
    }

    @Override
    public Agent getAssetParty() {
        return null;
    }

    @Override
    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    @Override
    public double getValue() {
        return amount;
    }

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        return null;
    }
}
