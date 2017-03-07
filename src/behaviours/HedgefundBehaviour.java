package behaviours;

import actions.Action;
import actions.PullFunding;
import actions.SellAsset;
import agents.Hedgefund;

import static java.lang.Math.max;

public class HedgefundBehaviour extends Behaviour {

    private Hedgefund hf;

    public HedgefundBehaviour(Hedgefund hf) {
        super(hf);
        this.hf = hf;
    }

    @Override
    protected void chooseActions() {
        if (hf.getHedgefundLeverageConstraint().isBelowBuffer()) {
            // If leverage is below buffer, we must de-lever

            double amountToDelever = hf.getHedgefundLeverageConstraint().getAmountToDelever();
            double maxLiabilitiesToPayOff = maxLiabilitiesToPayOff();
            double payLoan = 0.0;

            System.out.println();
            System.out.println("Amount to delever is " + String.format("%.2f", amountToDelever));


            if (maxLiabilitiesToPayOff == 0) {
                System.out.println("Strange! No liabilities to pay off.");
                return;
            }

            if (maxLiabilitiesToPayOff < amountToDelever) {
                System.out.println("Strange! We do not have enough liabilites to fully de-lever. " +
                        "We will de-lever an amount " + maxLiabilitiesToPayOff);
                amountToDelever = maxLiabilitiesToPayOff;
            }


        }

    }



}
