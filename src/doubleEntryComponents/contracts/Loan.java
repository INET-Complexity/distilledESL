package doubleEntryComponents.contracts;

import doubleEntryComponents.Agent;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.actions.CancelLoan;
import doubleEntryComponents.actions.PayLoan;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Loan extends Contract {

    public Loan(Agent assetParty, Agent liabilityParty, double principal) {
        this.assetParty = assetParty;
        this.liabilityParty = liabilityParty;
        this.principal = principal;
    }

    private Agent assetParty;
    private Agent liabilityParty;
    private double principal;

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
            availableActions.add(new CancelLoan(this));
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
}


