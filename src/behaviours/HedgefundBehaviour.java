package behaviours;

import actions.Action;
import actions.SellAsset;
import agents.Hedgefund;
import contracts.FailedMarginCallException;
import demos.Parameters;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HedgefundBehaviour extends Behaviour {

    private Hedgefund me;


    public HedgefundBehaviour(Hedgefund me) {
        super(me);
        this.me = me;
    }

    @Override
    protected void chooseActions() throws DefaultException {
        // 1) Pay matured cash commitments or default.
        double maturedPullFunding = me.getMaturedObligations();
        if (maturedPullFunding > 0) {
            System.out.println("We have matured payment contracts.obligations for a total of " + String.format("%.2f", maturedPullFunding));
            if (me.getCash() >= maturedPullFunding) {
                me.fulfilMaturedRequests();
            } else {
                System.out.println("A matured obligation was not fulfilled.");
                throw new DefaultException();
            }
        }

        // 2) Run margin calls; if any fail, default.
        try {
            me.runMarginCalls();
        } catch (FailedMarginCallException e) {
            System.out.println("A margin call failed.");
            throw new DefaultException();
        }

        // 3) If I'm insolvent, default.
        if (me.getLeverage() < me.getEffectiveMinLeverage()) {
            System.out.println("My leverage is "+me.getLeverage()+
                    " which is below the effective minimum "+me.getEffectiveMinLeverage());
            System.out.println("I'm dead.");
            throw new DefaultException();
        }

        // Compute amount to DeLever
        double amountToDelever =
                (me.getHedgefundLeverageConstraint().isBelowBuffer()) ?
                        me.getHedgefundLeverageConstraint().getAmountToDelever() :
                        0.0;

        // Compute liquidity to replenish (LCR buffer)
        double liquidityBufferToReplenish = min(0.0, me.getCashTarget() - me.getCash());
        double nonUrgentLiquidityNeeds = liquidityBufferToReplenish + amountToDelever;

        ArrayList<Double> cashCommitments = me.getCashCommitments();
        ArrayList<Double> cashInflows = me.getCashInflows();


        // ST PATRICK'S ALGORITHM
        // First loop
        // We look at timesteps between now and the time delay of PullFunding.

        double balance = me.getCash();
        for (int timeIndex = 0; timeIndex < Parameters.TIMESTEPS_TO_PAY; timeIndex++) {
            balance += cashInflows.get(timeIndex);
            balance -= cashCommitments.get(timeIndex);
        }

        if (balance < 0) {
            System.out.println("We will not be able to meet our cash commitments in the next " +
                    Parameters.TIMESTEPS_TO_PAY+ " timesteps, we will be missing an amount "+(-1.0*balance));

            double sellAssetsAmount = -1.0 * balance;
            sellAssetsProportionally(sellAssetsAmount);
            balance = 0.0;
        } else {
            System.out.println("We can meet our cash commitments in the next " +
                    Parameters.TIMESTEPS_TO_PAY+ " timesteps, and we will have a spare balance of "+balance);


            double deLever = min(balance, min(me.getCash()-me.getCashBuffer(), amountToDelever));
            if (deLever > 0) {
                System.out.println("We will use some cash to delever;");
                payOffLiabilities(deLever);
                balance -= deLever;
            }

        }

        // Second loop
        for (int timeIndex = Parameters.TIMESTEPS_TO_PAY; timeIndex < cashCommitments.size(); timeIndex++) {
            balance += cashInflows.get(timeIndex);
            balance -= cashCommitments.get(timeIndex);
        }

        balance -= nonUrgentLiquidityNeeds;
        raiseLiquidityWithPeckingOrder(balance);
    }

}
