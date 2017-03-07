package agents;

import actions.Action;
import actions.BankLeverageConstraint;
import actions.HedgefundLeverageConstraint;
import actions.SellAsset;
import contracts.*;

import java.util.ArrayList;
import java.util.HashSet;

public class Hedgefund extends Agent implements CanPledgeCollateral {

    private HedgefundLeverageConstraint hedgefundLeverageConstraint;

    public Hedgefund(String name) {
        super(name);
        this.hedgefundLeverageConstraint = new HedgefundLeverageConstraint(this);
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

    public double getEffectiveMinLeverage() {
        double totalRepo = mainLedger.getLiabilityValueOf(Repo.class);
        double averageHaircut = 0.0;

        HashSet<Contract> collateral = mainLedger.getAssetsOfType(CanBeCollateral.class);
        double totalCollateralValue = collateral.stream().mapToDouble(Contract::getValue).sum();

        for (Contract asset : collateral) {
            averageHaircut += ((CanBeCollateral) asset).getHairCut() * asset.getValue() / totalCollateralValue;
        }

        return totalRepo / averageHaircut;
    }

    public void withdrawCollateral(double excessValue, Repo repo) {
        repo.unpledgeProportionally(excessValue);
    }

    public void setBankLeverageConstraint(HedgefundLeverageConstraint hedgefundLeverageConstraint) {
        this.hedgefundLeverageConstraint = hedgefundLeverageConstraint;
    }

    public HedgefundLeverageConstraint getHedgefundLeverageConstraint() {
        return hedgefundLeverageConstraint;
    }

}
