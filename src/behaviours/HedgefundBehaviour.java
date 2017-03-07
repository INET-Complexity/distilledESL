package behaviours;

import actions.Action;
import actions.PullFunding;
import actions.SellAsset;
import agents.Hedgefund;
import contracts.Asset;

import java.util.ArrayList;

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
            double payLoan = 0.0;

            System.out.println();
            System.out.println("Amount to delever is " + String.format("%.2f", amountToDelever));

            // Assets are sold keeping the proportion of each asset in the balance sheet fixed.
            ArrayList<Action> sellAssetActions = getAllActionsOfType(SellAsset.class);
            //TODO What about assets that cannot be sold?
            
            double totalSellableAssets = sellAssetActions.stream()
                    .mapToDouble(Action::getMax)
                    .sum();

            if (totalSellableAssets < amountToDelever) {
                System.out.println("We cannot de-lever the full amount. We will delever as much as possible.");
            }

            for (Action action : sellAssetActions) {
                action.setAmount(amountToDelever * action.getMax() / totalSellableAssets);
                addAction(action);
            }

            // What about encumberance?!
        }

    }



}
