package behaviours;

import actions.Action;
import actions.PayLoan;
import actions.PullFunding;
import actions.SellAsset;
import agents.Hedgefund;

import java.util.ArrayList;

import static java.lang.Math.min;

public class HedgefundBehaviour extends Behaviour {

    private Hedgefund me;
    private double pendingToDeLever;


    public HedgefundBehaviour(Hedgefund me) {
        super(me);
        this.me = me;
        this.pendingToDeLever = 0.0;
    }

    @Override
    protected void chooseActions() {

        // 1) Check matured requests to pull funding. If we can't meet them right now, default.
        double maturedPullFunding = me.getPullFundingDue();
        if (maturedPullFunding > 0) {
            if(me.getCash() >= maturedPullFunding) {
                me.fulfilMaturedRequests();
            } else {
                //Todo: emergency procedure?
                triggerDefault();
            }
        }

        // 2) Check inbox for (non-matured) requests to pull funding, find out how much liquidity is needed,
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
        }

        double liquidityToRaise = totalPullFunding;

        // 4) If leverage is below buffer, we must de-lever further, and potentially raise liquidity.
        if (me.getHedgefundLeverageConstraint().isBelowBuffer()) {
            double amountToDelever = me.getHedgefundLeverageConstraint().getAmountToDelever();
            System.out.println("\nWe are below our effective minimum leverage. Amount to de-lever: " + amountToDelever);
            double availableNow = min(me.getCash(), amountToDelever);

            System.out.println((availableNow == 0) ?
                    "We have no cash -> we cannot use cash to de-lever"
                    : (availableNow < amountToDelever) ?
                    "We can de-lever an amount " + availableNow + " by using cash."
                    : "We can de-lever fully using cash -> No contagion!");

            if (availableNow > 0) payOffLiabilities(availableNow);
            liquidityToRaise += amountToDelever - availableNow;
        }

        // 4) If we decided we need to raise liquidity, we go through our available actions and select a set of actions
        // that will raise the required liquidity.
        if (liquidityToRaise > 0) {

            ArrayList<Action> sellAssetActions = getAllActionsOfType(SellAsset.class);
            double totalSellableAssets = sellAssetActions.stream()
                    .mapToDouble(Action::getMax)
                    .sum();

            if (totalSellableAssets < liquidityToRaise) {
                System.out.println("We cannot raise enough liquidity. We will raise as much as possible.");
                liquidityToRaise = totalSellableAssets;
            }

            for (Action action : sellAssetActions) {
                action.setAmount(liquidityToRaise * action.getMax() / totalSellableAssets);
                addAction(action);
            }
        }

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
