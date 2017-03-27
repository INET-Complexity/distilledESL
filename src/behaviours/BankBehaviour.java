package behaviours;

import agents.Bank;
import contracts.FailedMarginCallException;
import demos.Model;
import demos.Parameters;

import java.util.ArrayList;

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
            System.out.println("We have matured payment obligations for a total of " + String.format("%.2f", maturedPullFunding));
            if (me.getCash() >= maturedPullFunding - 0.02) {
                me.fulfilMaturedRequests();
            } else {
                System.out.println("A matured obligation was not fulfilled.\nDEFAULT DUE TO LACK OF LIQUIDITY");
                throw new DefaultException(me, DefaultException.TypeOfDefault.LIQUIDITY);
            }
        }

        // 2) Run margin calls; if any fail, default.
        try {
            me.runMarginCalls();
        } catch (FailedMarginCallException e) {
            System.out.println("A margin call failed.");
            throw new DefaultException(me, DefaultException.TypeOfDefault.FAILED_MARGIN_CALL);
        }

        // Revalue all loans using the NEKO model
        if (Parameters.NEKO_MODEL) me.revalueAllLoans();

        // 3) If I'm insolvent, default.
        if (me.getLeverage() <= Parameters.BANK_LEVERAGE_MIN) {
            System.out.println("My leverage is "+me.getLeverage()+
                    " which is below the minimum "+Parameters.BANK_LEVERAGE_MIN);
            System.out.println("DEFAULT DUE TO INSOLVENCY.");
            throw new DefaultException(me, DefaultException.TypeOfDefault.SOLVENCY);
        }

        // Compute amount to DeLever
        double amountToDelever =
                (me.getBankLeverageConstraint().isBelowBuffer()) ?
                        me.getBankLeverageConstraint().getAmountToDelever() :
                            0.0;


        ArrayList<Double> cashCommitments = me.getCashCommitments();
        ArrayList<Double> cashInflows = me.getCashInflows();

        System.out.println("\nLiquidity management for this timestep");
        System.out.println("Current unencumbered cash -> "+me.getCash());
        System.out.println("LCR buffer -> "+me.getCashBuffer());
        System.out.println("Needed to delever -> "+amountToDelever);
//        System.out.println("Needed to replenish the LCR buffer -> "+liquidityBufferToReplenish);
        System.out.println("Needed to fulfil obligations -> "+cashCommitments.stream().mapToDouble(Double::doubleValue).sum());
        System.out.println("Expected cash inflows -> "+cashInflows.stream().mapToDouble(Double::doubleValue).sum());
        System.out.println();



        // ST PATRICK'S ALGORITHM
        // First loop
        // We look at timesteps between now and the time delay of PullFunding.

        double balance = me.getCash();
        double miniumSpareBalanceInThePeriod = balance;
        for (int timeIndex = 0; timeIndex < Parameters.TIMESTEPS_TO_PAY+1; timeIndex++) {
            balance += cashInflows.get(timeIndex);
            balance -= cashCommitments.get(timeIndex);


            if (balance < 0) {
                System.out.println("At timestep "+(timeIndex+ Model.getTime()+1)+", we will be short of liquidity, since our expected balance " +
                        "will be "+balance);
                System.out.println("We must firesale assets now.");
                double sellAssetsAmount = -1.0 * balance;
                double amountSold = sellAssetsProportionally(sellAssetsAmount);
                balance += amountSold;
                if (balance < 0) System.out.println("We won't be able to firesale enough assets. We'll wait and see but might default.");
            }

            miniumSpareBalanceInThePeriod = Math.min(miniumSpareBalanceInThePeriod, balance);

        }

        if (balance >= 0) {
            System.out.println("We can meet our cash commitments in the next " +
                    Parameters.TIMESTEPS_TO_PAY + " timesteps, and we will have a spare balance of " + balance);
            System.out.println("Our minimum spare balance in the period will be "+miniumSpareBalanceInThePeriod);
        }

        double deLever = Math.min(miniumSpareBalanceInThePeriod, min(me.getCash()-me.getCashBuffer(), amountToDelever));

        if (deLever > 0) {
            System.out.println("Since we would like to delever an amount "+amountToDelever +
                    "\n\tand we have an amount of cash above the buffer of "+ (me.getCash()-me.getCashBuffer()) +
                    "\n\tand we expect our minimum spare cash balance after paying approaching obligations to be "+miniumSpareBalanceInThePeriod +
                    "\n\twe can use an amount "+deLever+" to delever.");
            deLever = payOffLiabilities(deLever);
            amountToDelever -= deLever;
        }
        balance -= deLever;


        // Second loop
        miniumSpareBalanceInThePeriod = balance;
        for (int timeIndex = Parameters.TIMESTEPS_TO_PAY+1; timeIndex < cashCommitments.size(); timeIndex++) {
            balance += cashInflows.get(timeIndex);
            balance -= cashCommitments.get(timeIndex);

            if (balance < 0) {
                System.out.println("At timestep "+(timeIndex+ Model.getTime()+1)+", we will be short of liquidity, since our expected balance " +
                        "will be "+balance);
                System.out.println("We must raise liquidity with pecking order now.");
                double peckingOrderAmount = -1.0 * balance;
                double amountRaised = raiseLiquidityWithPeckingOrder(peckingOrderAmount);
                balance += amountRaised;

                if (balance < 0) System.out.println("We won't be able to raise enough liquidity. We'll wait and see.");
            }

            miniumSpareBalanceInThePeriod = Math.min(miniumSpareBalanceInThePeriod, balance);

        }

        System.out.println("\nOur expected balance after delevering and including long term obligations is now "+balance +
                "\n\twe have "+amountToDelever+" left to delever");

        balance -= amountToDelever;

        if (balance < 0) {
            double liquidityToRaise = -1.0 * balance;
            liquidityToRaise += me.getCashTarget();
            System.out.println("We will use up all our remaining liquidity to delever, so will also replenish up to the cash target of "+me.getCashTarget());


            System.out.println("In order to meet our long-term cash commitments and non-urgent liquidity needs, " +
                    "we will raise liquidity: "+liquidityToRaise);
            raiseLiquidityWithPeckingOrder(liquidityToRaise);


        } else {
            System.out.println("We can meet our long-term cash commitments and non-urgent liquidity needs in the next " +
                cashCommitments.size()+ " timesteps, and we will have a spare balance of "+balance);

            deLever = min(balance, min(me.getCash()-me.getCashBuffer(), amountToDelever));
            if (deLever > 0) {
                System.out.println("Since we would like to delever an amount "+amountToDelever +
                        "\nand we have an amount of cash above the buffer of "+ (me.getCash()-me.getCashBuffer()) +
                        "\nand we expect our cash balance after paying approaching obligations to be "+balance +
                        ",\n we can use an amount "+deLever+" to delever.");
                payOffLiabilities(deLever);
            }

            balance -= deLever;
            if (balance < me.getCashBuffer()) {
                System.out.println("We expect our balance "+balance+" in the end to be below the cash buffer." +
                        " We will replenish to the target of "+me.getCashTarget());
                double liquidityToRaise = me.getCashTarget() - balance;
                raiseLiquidityWithPeckingOrder(liquidityToRaise);
            }

        }

    }

}
