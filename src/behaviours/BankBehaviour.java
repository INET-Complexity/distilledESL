package behaviours;

import actions.Action;
import actions.PayLoan;
import actions.PullFunding;
import actions.SellAsset;
import agents.Bank;
import contracts.FailedMarginCallException;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BankBehaviour extends Behaviour {

    private Bank me;
    private double pendingToDeLever;

    public BankBehaviour(Bank me) {
        super(me);
        this.me = me;
        this.pendingToDeLever = 0.0;
    }

    @Override
    protected void chooseActions() {

        // Check inbox for matured PullFunding requests. If we can't meet them right now, default.
        double maturedPullFunding = me.getMaturedObligations();
        if (maturedPullFunding > 0) {
            System.out.println("We have matured payment contracts.obligations for a total of "+String.format("%.2f", maturedPullFunding));
            if (me.getCash() >= maturedPullFunding) {
                me.fulfilMaturedRequests();
            } else {
                //Todo: emergency procedure?
                triggerDefault();
                return;
            }
        }

        // Check inbox for other PullFunding requests, find out how much liquidity is needed,
        // and pay all of them now if possible.
        double totalPullFunding = me.getPendingObligations();
        if (totalPullFunding > 0) {
            System.out.println("We have not-yet-matured payment contracts.obligations for a total of "+String.format("%.2f", totalPullFunding));
            if (me.getCash() >= totalPullFunding) {
                me.fulfilAllRequests();
                totalPullFunding = 0.0;
            }
        }

        // Run margin calls and pledge/unpledge collateral as needed. If any margin call fails, default.
        try {
            me.runMarginCalls();
        } catch (FailedMarginCallException e) {
            // If any margin call failed, DEFAULT!
            triggerDefault(); //TODO; how to abort execution of 'chooseAvailableActions'?
            return;
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
        liquidityToRaise -= me.getPendingObligations();

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


    private double getLiquidityAboveLCR() {
        return max(me.getCash() - me.getLCR_constraint().getCashTarget(), 0.0);
    }


    public void triggerDefault() {

    }

}
