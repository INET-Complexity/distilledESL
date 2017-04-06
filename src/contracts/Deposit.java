package contracts;

import actions.Action;
import agents.StressAgent;
import economicsl.Agent;
import demos.Parameters;

import java.util.Collections;
import java.util.List;

public class Deposit extends ContractStress {

    private StressAgent depositor;
    private StressAgent holder;
    private double amount;

    @Override
    public double getLCRweight() {return Parameters.DEPOSITS_LCR;}

    public Deposit(StressAgent depositor, StressAgent holder, double amount) {
        this.depositor = depositor;
        this.holder = holder;
        this.amount = amount;
    }

    @Override
    public String getName(Agent me) {
        return "Deposits";
    }

    @Override
    public StressAgent getAssetParty() {
        return depositor;
    }

    @Override
    public StressAgent getLiabilityParty() {
        return holder;
    }

    @Override
    public double getValue(Agent me) {
        return amount;
    }

    @Override
    public List<Action> getAvailableActions(Agent me) {
        return Collections.emptyList();
    }
}
