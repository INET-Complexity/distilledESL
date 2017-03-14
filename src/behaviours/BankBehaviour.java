package behaviours;

import agents.Bank;
import actions.Action;
import actions.PullFunding;
import actions.PayLoan;
import actions.SellAsset;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BankBehaviour extends Behaviour {

    public Bank me;
    private double pendingToDeLever;
    private double pendingPullFunding;

    public BankBehaviour(Bank me) {
        super(me);
        this.me = me;
        this.pendingToDeLever = 0.0;
        this.pendingPullFunding = 0.0;
    }

    @Override
    protected void chooseActions() {

        // 1) Check inbox for matured PullFunding requests. If we can't meet them right now, default.
        double maturedPullFunding = me.getMaturedPullFunding();
        if (maturedPullFunding > 0) {
            if(me.getCash() >= maturedPullFunding) {
                me.fulfilMaturedRequests();
            } else {
                //Todo: emergency procedure?
                triggerDefault();
            }
        }

        // 2) Check inbox for other PullFunding requests, find out how much liquidity is needed,
        // and pay all of them now if possible.
        double totalPullFunding = me.getTotalPullFunding();
        if (totalPullFunding > 0) {
           if(me.getCash() >= totalPullFunding) {
               me.fulfilAllRequests();
               totalPullFunding = 0.0;
           }
        }

        // 3) If we were trying to de-lever further from the previous timestep, we do it now. We break the LCR
        // constraint if needed.
        if (pendingToDeLever > 0) {
            double amountToDelever = min(pendingToDeLever, me.getCash());
            if (amountToDelever > 0) payOffLiabilities(amountToDelever);
            pendingToDeLever = 0.0;
        }

        double liquidityToRaise = totalPullFunding;

        // 4) If leverage is below buffer, we must de-lever further, and potentially raise liquidity.
        if (me.getBankLeverageConstraint().isBelowBuffer()) {
            double amountToDelever = me.getBankLeverageConstraint().getAmountToDelever();
            System.out.println("\nWe are below the leverage buffer. Amount to de-lever: " + amountToDelever);
            double liquidityAboveLCR = getLiquidityAboveLCR();
            double availableNow = min(liquidityAboveLCR, amountToDelever);

            System.out.println((availableNow == 0) ?
                    "We are at or below the LCR buffer -> we cannot use cash to de-lever"
                    : (availableNow < amountToDelever) ?
                    "We can de-lever an amount " + availableNow + " by using cash."
                    : "We can de-lever fully using cash -> No contagion!");

            if (availableNow > 0) payOffLiabilities(availableNow);
            liquidityToRaise += amountToDelever - availableNow;
        }

        // 3) If we used up some of our cash buffer, we try to replenish it, so we add to the liquidity to raise.
        liquidityToRaise += me.getLCR_constraint().getLiquidityToRaise();

        // Discount the liquidity that we are expecting from pull funding requests not yet met by the counter-parties.
        liquidityToRaise -= me.getPendingPayments();

        // 4) If we decided we need to raise liquidity, we go through our available actions and select a set of actions
        // that will raise the required liquidity.
        if (liquidityToRaise > 0) {

            // Look through the actions and pick according to the pecking order
            while (liquidityToRaise > 0.0 && actionsLeft()) {
                Action nextAction = findActionOfType(PullFunding.class);
                if (nextAction == null) {
                    nextAction = findActionOfType(SellAsset.class);
                    if (nextAction == null) {
                        // We can't find any more suitable actions. Stop looking.
                        break;
                    }
                }

                double liquidityFromThisAction = min(nextAction.getMax(), liquidityToRaise);
                nextAction.setAmount(liquidityFromThisAction);
                addAction(nextAction);
                liquidityToRaise -= liquidityFromThisAction;

            }

            System.out.println((liquidityToRaise > 0) ?
                    "We could not find a set of actions to raise enough liquidity. We can only expect to raise " + pendingToDeLever
                    : "We found a set of actions to raise enough liquidity!");
        }

    }

//
//    /**
//     *
//     * @param liquidityExpected the liquidity that we tried to raise in the previous time-step. Note that we don't know
//     *                          if we succeeded in raising it since we don't know 1) whether the funding we tried
//     *                          to pull was actually paid back, and 2) what price we obtained from the sale of assets
//     * @return the amount of liquidity we can use to de-lever
//     */
//    private double liquidityRaised(double liquidityExpected) {
//        double amountToDelever = 0.0;
//
//        if (liquidityExpected > 0) {
//            // We raised some liquidity from the previous time-step; we must use it to de-lever now.
//
//            if (getLiquidityAboveLCR() > liquidityExpected) {
//                // We have enough liquidity; we use it.
//                amountToDelever = liquidityExpected;
//            } else {
//                // We don't have as much liquidity as we expected.
//                System.out.println("We did not raise as much liquidity as we expected.\nExpected: "+
//                        liquidityExpected +"\nAvailable: "+(me.getCash()-me.getLCR_constraint().getCashTarget()));
//
//                if (me.getCash() > liquidityExpected) {
//                    // If we have enough cash, we use up the LCR buffer and de-lever the expected amount.
//                    System.out.println("We will use some of the LCR buffer to de-lever");
//                    amountToDelever = liquidityExpected;
//                } else {
//                    // If we don't have enough cash, we use it all up.
//                    System.out.println("We do not have enough cash to de-lever as much as expected. We will deplete the cash reserve.");
//                    amountToDelever = me.getCash();
//                }
//            }
//
//        }
//        return amountToDelever;
//    }

    private double getLiquidityAboveLCR() {
        return max(me.getCash() - me.getLCR_constraint().getCashTarget(), 0.0);
    }


    public void triggerDefault() {
        ArrayList<Action> availableActions = me.getAvailableActions(me);
        for (Action action : availableActions ) {
            // Sell every asset!
            if (action instanceof SellAsset) {
                action.setAmount(action.getMax());
                action.perform();
            } else if (action instanceof PayLoan) {
                // Loans must be terminated and changed into assets.
                ((PayLoan)action).getLoan().liquidate();
            } // all other actions are ignored
        }

    }

}
