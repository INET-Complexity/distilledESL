package contracts;

import agents.Agent;
import actions.Action;
import actions.PullFunding;
import actions.PayLoan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void payLoan(double amount) {
        if (liabilityParty!= null) liabilityParty.payLoan(amount, this);
        if (assetParty!= null) assetParty.pullFunding(amount, this);
        reducePrincipal(amount);
    }

    private void reducePrincipal(double amount) {
        assert(amount <= principal);
        principal -= amount;

        if (principal < 0.01) {
            System.out.println("This loan has been fully repaid.");
            //Todo: and now what shall we do? Destroy the loan?
        }

    }

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(principal > 0)) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        if (assetParty==me) {
            availableActions.add(new PullFunding(this));
        } else if (liabilityParty==me){
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

    public double getValue() {
        return principal;
    }

    public void liquidate() {
        assetParty.liquidateLoan(getValue(), VALUE_GIVEN_DEFAULT, this);
        principal = 0.0;
    }

}


