package contracts;

import actions.Action;
import agents.StressAgent;
import economicsl.Agent;
import demos.Parameters;

import java.util.ArrayList;
import java.util.List;

public class LongTermUnsecured extends ContractStress {

    private StressAgent liabilityParty;
    private double amount;

    public LongTermUnsecured(StressAgent liabilityParty, double amount) {
        this.amount = amount;
        this.liabilityParty = liabilityParty;
    }

    @Override
    public double getLCRweight() {
        return Parameters.LONG_TERM_LCR;
    }

    @Override
    public String getName(Agent me) {
        return "Long term unsecured liabilities";
    }

    @Override
    public StressAgent getAssetParty() {
        return null;
    }

    @Override
    public StressAgent getLiabilityParty() {
        return liabilityParty;
    }


    @Override
    public double getValue(Agent me) {return amount;}

    @Override
    public List<Action> getAvailableActions(Agent me) {

        return new ArrayList<>();
    }

}
