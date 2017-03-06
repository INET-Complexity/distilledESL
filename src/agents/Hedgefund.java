package agents;

import actions.Action;
import actions.LCR_Constraint;
import actions.LeverageConstraint;
import actions.SellAsset;
import contracts.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class Hedgefund extends Agent implements CanPledgeCollateral {

    private LeverageConstraint leverageConstraint;

    public Hedgefund(String name) {
        super(name);
        this.leverageConstraint = new LeverageConstraint(this);
    }

    //Todo: is this the best way to do this? This should really be in Behaviour
    public void raiseLiquidity(double liquidityNeeded) {
        ArrayList<Action> availableActions = getAvailableActions(this);

        double initialAssetHoldings = mainLedger.getAssetValueOf(Asset.class);

        for (Action action : availableActions) {
            if (action instanceof SellAsset) {
                action.setAmount(action.getMax()*liquidityNeeded/initialAssetHoldings);
                action.print();
                action.perform();
            }
        }

    }

    @Override
    public void putMoreCollateral(double total, Repo repo) {
        // First, get a set of all my Assets that can be pledged as collateral
        HashSet<Contract> potentialCollateral = mainLedger.getAssetsOfType(AssetCollateral.class);

        double maxHaircutValue = 0.0;
        for (Contract contract : potentialCollateral) {
            assert(contract instanceof CanBeCollateral);
            CanBeCollateral asset = (CanBeCollateral) contract;
            maxHaircutValue += asset.getMaxEncumberableValue() * (1.0 - asset.getHairCut());
        }

        for (Contract contract : potentialCollateral) {
            CanBeCollateral asset = (CanBeCollateral) contract;

            double quantityToPledge = total * asset.getMaxEncumberableValue() * (1.0 - asset.getHairCut()) / maxHaircutValue;
            repo.pledgeCollateral(asset, quantityToPledge);

        }
    }


    public void withdrawCollateral(double excessValue, Repo repo) {
        repo.unpledgeProportionally(excessValue);
    }

    public void setLeverageConstraint(LeverageConstraint leverageConstraint) {
        this.leverageConstraint = leverageConstraint;
    }

    public LeverageConstraint getLeverageConstraint() {
        return leverageConstraint;
    }

}
