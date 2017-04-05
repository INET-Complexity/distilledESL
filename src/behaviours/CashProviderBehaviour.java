package behaviours;

import actions.Action;
import actions.PullFunding;
import agents.StressAgent;
import agents.Agent;
import agents.Bank;
import demos.Parameters;

import java.util.ArrayList;
import java.util.HashMap;

public class CashProviderBehaviour extends Behaviour {

    private HashMap<Bank, Integer> trialPeriod = new HashMap<>();
    public CashProviderBehaviour(StressAgent me) {
        super(me);
    }

    /**
     * Cash providers 'run' from Banks whose leverage is below a threshold. In those cases, they pull a fixed
     * fraction of their funding to the bank.
     */
    @Override
    protected void chooseActions() {
        if (Parameters.CASH_PROVIDER_RUNS) {

            trialPeriod.forEach((bank, days) -> trialPeriod.put(bank, days-1));
            trialPeriod.values().removeIf(value -> value <= 0);


            // Get all the pull funding actions available
            ArrayList<Action> pullFundingActions = getAllActionsOfType(PullFunding.class);

            for (Action action : pullFundingActions) {
                PullFunding pullFundingAction = (PullFunding) action;
                Agent borrower = pullFundingAction.getLoan().getLiabilityParty();
                assert(borrower instanceof Bank);
                Bank bank = (Bank) borrower;


                // Check if the leverage of the bank we are lending money to is below the threshold.
                if (bank.getLeverage() < Parameters.LEVERAGE_THRESHOLD_TO_RUN
                        || bank.getLCR() < Parameters.LCR_THRESHOLD_TO_RUN) {

                    if (trialPeriod.containsKey(bank)) {
                        System.out.printf(bank.getName()+" has not gone above its Leverage and/or LCR threshold yet, "+
                        "but it still has "+trialPeriod.get(bank)+" timesteps left in the trial period to do so.");
                    } else {
                        // If it is, withdraw a fixed fraction of funding to this bank.
                        System.out.println(bank.getName()+" has LCR "+bank.getLCR()+", and my threshold to run is "+Parameters.LCR_THRESHOLD_TO_RUN +
                        "\n\tit has leverage "+bank.getLeverage()+", and my threshold to run is "+Parameters.LEVERAGE_THRESHOLD_TO_RUN);
                        trialPeriod.put(bank, Parameters.TRIAL_PERIOD);
                        pullFundingAction.setAmount(pullFundingAction.getMax() * Parameters.CP_FRACTION_TO_RUN);
                        pullFundingAction.perform();
                    }
                }

            }
        }
    }
}
