package behaviours;

import actions.Action;
import actions.PullFunding;
import actions.SellAsset;
import agents.Bank;
import contracts.FailedMarginCallException;
import demos.Parameters;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BankBehaviour extends Behaviour {

    private Bank me;

    public BankBehaviour(Bank me) {
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
        if (me.getLeverage() < Parameters.BANK_LEVERAGE_MIN) {
            System.out.println("My leverage is "+me.getLeverage()+
                    " which is below the minimum "+Parameters.BANK_LEVERAGE_MIN);
            System.out.println("I'm dead.");
            throw new DefaultException();
        }

        // Compute amount to DeLever
        double amountToDelever =
                (me.getBankLeverageConstraint().isBelowBuffer()) ?
                        me.getBankLeverageConstraint().getAmountToDelever() :
                            0.0;

        // Compute liquidity to replenish (LCR buffer)
        double liquidityBufferToReplenish = me.getLCR_constraint().getLiquidityToRaise();
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


            double deLever = min(balance, min(me.getCash()-me.getLCR_constraint().getCashBuffer(), amountToDelever));
            if (deLever > 0) {
                System.out.println("We will use an amount "+deLever+" to delever.");
                payOffLiabilities(deLever);
                amountToDelever -= deLever;
            }
            balance -= deLever;

        }

        // Second loop
        for (int timeIndex = Parameters.TIMESTEPS_TO_PAY; timeIndex < cashCommitments.size(); timeIndex++) {
            balance += cashInflows.get(timeIndex);
            balance -= cashCommitments.get(timeIndex);
        }

        balance -= nonUrgentLiquidityNeeds;

        if (balance < 0) {
            double liquidityToRaise = -1.0 * balance;
            System.out.println("In order to meet our long-term cash commitments and non-urgent liquidity needs, " +
                    "we will raise liquidity: "+liquidityToRaise);
            raiseLiquidityWithPeckingOrder(liquidityToRaise);
        } else {
            System.out.println("We can meet our long-term cash commitments in the next " +
                cashCommitments.size()+ " timesteps, and we will have a spare balance of "+balance);

            double deLever = min(balance, min(me.getCash()-me.getLCR_constraint().getCashBuffer(), amountToDelever));
            if (deLever > 0) {
                System.out.println("We will use an amount "+deLever+" to delever.");
                payOffLiabilities(deLever);
            }
        }

    }

}
