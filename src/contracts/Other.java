package contracts;

import actions.Action;
import agents.StressAgent;
import economicsl.Agent;

import java.util.Collections;
import java.util.List;

public class Other extends ContractStress {
    private StressAgent assetParty;
    private StressAgent liabilityParty;
    private double amount;

    public Other(StressAgent assetParty, StressAgent liabilityParty, double amount) {
        this.assetParty = assetParty;
        this.liabilityParty = liabilityParty;
        this.amount = amount;
    }

    @Override
    public String getName(Agent me) {
        return "Other";
    }

    @Override
    public StressAgent getAssetParty() {
        return assetParty;
    }

    @Override
    public StressAgent getLiabilityParty() {
        return liabilityParty;
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
