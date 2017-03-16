package agents;

import actions.HedgefundLeverageConstraint;
import behaviours.Behaviour;
import behaviours.HedgefundBehaviour;
import contracts.*;

import java.util.HashSet;

public class Hedgefund extends Agent implements CanPledgeCollateral {

    private HedgefundBehaviour behaviour;
    private HedgefundLeverageConstraint hedgefundLeverageConstraint;
    public final double cashBuffer = 15.0;
    //Todo just a number for the moment.

    public Hedgefund(String name) {
        super(name);
        this.hedgefundLeverageConstraint = new HedgefundLeverageConstraint(this);
        this.behaviour = new HedgefundBehaviour(this);
    }


    @Override
    public void putMoreCollateral(double total, Repo repo) {
        // First, get a set of all my Assets that can be pledged as collateral
        HashSet<Contract> potentialCollateral = mainLedger.getAssetsOfType(AssetCollateral.class);

        double maxHaircutValue = getMaxUnencumberedHaircuttedCollateral();

        for (Contract contract : potentialCollateral) {
            CanBeCollateral asset = (CanBeCollateral) contract;

            double quantityToPledge = total * asset.getUnencumberedValue() * (1.0 - asset.getHaircut()) / maxHaircutValue;
            repo.pledgeCollateral(asset, quantityToPledge);

        }
    }

    @Override
    public double getMaxUnencumberedHaircuttedCollateral() {
        return mainLedger.getAssetsOfType(AssetCollateral.class).stream()
                .mapToDouble(asset ->
                        ((CanBeCollateral) asset).getUnencumberedValue() *
                                (1.0 - ((CanBeCollateral) asset).getHaircut()))
                                    .sum();
    }

    public double getEffectiveMinLeverage() {
        double totalRepo = mainLedger.getLiabilityValueOf(Repo.class);
        double averageHaircut = 0.0;

        HashSet<Contract> collateral = mainLedger.getAssetsOfType(CanBeCollateral.class);
        double totalCollateralValue = collateral.stream().mapToDouble(Contract::getValue).sum();

        for (Contract asset : collateral) {
            averageHaircut += ((CanBeCollateral) asset).getHaircut() * asset.getValue() / totalCollateralValue;
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

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }
}
