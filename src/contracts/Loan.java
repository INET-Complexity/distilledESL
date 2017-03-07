package contracts;

import agents.Agent;
import agents.Bank;
import actions.Action;
import actions.PullFunding;
import actions.PayLoan;

import java.util.ArrayList;

public class Loan extends Contract {
    private static final double VALUE_GIVEN_DEFAULT = 0.30;

    public Loan(Agent assetParty, Agent liabilityParty, double principal) {
        this.assetParty = assetParty;
        this.liabilityParty = liabilityParty;
        this.principal = principal;
    }

    Agent assetParty;
    Agent liabilityParty;
    double principal;

    public void reducePrincipal(double amount) {
        assert(amount <= principal);
        principal -= amount;

        if (principal < 0.01) {
            System.out.println("This loan has been fully repaid.");
            //Todo: and now what shall we do? Destroy the loan?
        }

    }

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = new ArrayList<>();
        if (assetParty==me) {
            availableActions.add(new PullFunding(this));
        } else if (liabilityParty == me) {
            availableActions.add(new PayLoan(this));
        }
        return availableActions;
    }

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    @Override
    public double getValue() {
        return principal;
    }

    public void liquidate() {
        ((Bank) assetParty).liquidateLoan(getValue(), VALUE_GIVEN_DEFAULT, this);
        principal = 0.0;
    }
}


