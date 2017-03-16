package behaviours;

import actions.Action;
import actions.PayLoan;
import actions.SellAsset;
import agents.Hedgefund;

import java.util.ArrayList;

import static java.lang.Math.max;
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
        double maturedPullFunding = me.getMaturedObligations();
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
        double totalPullFunding = me.getPendingObligations();
        if (totalPullFunding > 0) {
            if(me.getCash() >= totalPullFunding) {
                me.fulfilAllRequests();
                totalPullFunding = 0.0;
            }
        }

        // 3) If we were trying to de-lever further from the previous timestep, we do it now.
        if (pendingToDeLever > 0) {
            double amountToDelever = min(pendingToDeLever, me.getCash());
            if (amountToDelever > 0) payOffLiabilities(amountToDelever);
        }

        double liquidityToRaise = totalPullFunding;

        System.out.println("\nCurrent leverage: " + String.format("%.2f", me.getLeverage()) +
            ", minimum leverage: " + String.format("%.2f", me.getEffectiveMinLeverage()) +
            ", leverage buffer: " + String.format("%.2f", me.getHedgefundLeverageConstraint().getLeverageBuffer())) ;
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
        } else {
            System.out.println("We are above the leverage buffer, no need to act.");
        }

        // 5) We try to raise an extra amount of liquidity, to replenish the liquidity buffer.
        double replenishBuffer = (me.getCash() < me.getCashBuffer()) ? me.getCashTarget() - me.getCash() : 0.0;

        if (replenishBuffer >0 ) System.out.println("\nCurrent liquidity is " + me.getCash() +
            ", cash buffer is " + me.getCashBuffer() +
                ", cash target is "+me.getCashTarget()+", we must raise an amount " + replenishBuffer);

        liquidityToRaise += replenishBuffer;

        // 6) If we decided we need to raise liquidity, we need to select a set of actions that will raise that liquidity.
        // A hedgefund's actions include just firesales of unencumbered assets, which it performs proportionally.
        if (liquidityToRaise > 0) {

            System.out.println("\nRaising a total liquidity of "+liquidityToRaise);

            ArrayList<Action> sellAssetActions = getAllActionsOfType(SellAsset.class);
            double totalSellableAssets = sellAssetActions.stream()
                    .mapToDouble(Action::getMax)
                    .sum();

            if (totalSellableAssets < liquidityToRaise) {
                System.out.println("We cannot raise enough liquidity. We will raise as much as possible.");
                liquidityToRaise = totalSellableAssets;
            }

            for (Action action : sellAssetActions) {
                if (action.getMax()>0) { // Todo: To prevent selling encumbered assets
                    action.setAmount(liquidityToRaise * action.getMax() / totalSellableAssets);
                    addAction(action);
                }
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
