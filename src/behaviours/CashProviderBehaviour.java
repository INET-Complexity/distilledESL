package behaviours;

import actions.Action;
import actions.PullFunding;
import agents.Agent;
import demos.Parameters;

import java.util.ArrayList;

public class CashProviderBehaviour extends Behaviour {

    public CashProviderBehaviour(Agent me) {
        super(me);
    }

    /**
     * Cash providers 'run' from Banks whose leverage is below a threshold. In those cases, they pull a fixed
     * fraction of their funding to the bank.
     */
    @Override
    protected void chooseActions() {
        if (Parameters.CASH_PROVIDER_RUNS) {
            // Get all the pull funding actions available
            ArrayList<Action> pullFundingActions = getAllActionsOfType(PullFunding.class);

            for (Action action : pullFundingActions) {
                PullFunding pullFundingAction = (PullFunding) action;

                // Check if the leverage of the bank we are lending money to is below the threshold.
                if (pullFundingAction.getLoan().getLiabilityParty().getLeverage() < Parameters.LEVERAGE_THRESHOLD_TO_RUN) {
                    // If it is, withdraw a fixed fraction of funding to this bank.
                    pullFundingAction.setAmount(pullFundingAction.getMax() * Parameters.FRACTION_TO_RUN);
                    addAction(pullFundingAction);
                }

            }
        }
    }
}
