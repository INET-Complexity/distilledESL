package agents;

import actions.HedgefundLeverageConstraint;
import behaviours.Behaviour;
import behaviours.HedgefundBehaviour;
import contracts.*;
import demos.Parameters;

import java.util.HashSet;

public class Hedgefund extends Agent implements CanPledgeCollateral {

    private HedgefundBehaviour behaviour;
    private HedgefundLeverageConstraint hedgefundLeverageConstraint;
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
        double haircuttedValuePledgedSoFar = 0.0;

        for (Contract contract : potentialCollateral) {
            CanBeCollateral asset = (CanBeCollateral) contract;

            double quantityToPledge = asset.getUnencumberedQuantity() * total / maxHaircutValue;
            repo.pledgeCollateral(asset, quantityToPledge);
            haircuttedValuePledgedSoFar += quantityToPledge * asset.getPrice() * (1.0 - asset.getHaircut());

        }

        repo.pledgeCashCollateral(total - haircuttedValuePledgedSoFar);
    }


    @Override
    public double getMaxUnencumberedHaircuttedCollateral() {
        return mainLedger.getAssetsOfType(AssetCollateral.class).stream()
                .mapToDouble(asset ->
                        ((CanBeCollateral) asset).getUnencumberedValue() *
                                (1.0 - ((CanBeCollateral) asset).getHaircut()))
                .sum() + getCash();
    }

    public double getEffectiveMinLeverage() {
        HashSet<Contract> collateral = mainLedger.getAssetsOfType(CanBeCollateral.class);
        double totalCollateralValue = collateral.stream().mapToDouble(Contract::getValue).sum();

        double effectiveAverageHaircut = 0.0;

        for (Contract asset : collateral) {
            effectiveAverageHaircut += ((CanBeCollateral) asset).getHaircut() * asset.getValue() / totalCollateralValue;
        }

        return effectiveAverageHaircut;
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

    public double getCashBuffer() {
        return getAssetValue() * Parameters.HF_CASH_BUFFER_AS_FRACTION_OF_ASSETS;
    }

    public double getCashTarget() {
        return getAssetValue() * Parameters.HF_CASH_TARGET_AS_FRACTION_OF_ASSETS;
    }

    @Override
    public void triggerDefault() {
        super.triggerDefault();

        HashSet<Contract> repos = mainLedger.getLiabilitiesOfType(Repo.class);
        for (Contract repo : repos) {
            ((Repo) repo).liquidate();
        }

    }

    @Override
    public double getLCR() {
        //the LCR for a hedgefund is just the cash...
        return getCash();
    }
}
