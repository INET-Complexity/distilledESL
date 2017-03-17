package contracts;

import agents.Agent;
import agents.CanPledgeCollateral;
import demos.Parameters;

import java.util.*;

/**
 * A Repo is a securitized Loan, i.e. it includes collateral. Collateral is stored as hashmap of CanBeCollateral contracts
 * and quantities of each. The Repo can value its collateral by valuing each contract in the Collateral hashmap, and
 * weighting each by its haircut.
 *
 *
 * @author rafa
 */
public class Repo extends Loan {

    public Repo(Agent assetParty, Agent liabilityParty, double principal) {
        super(assetParty, liabilityParty, principal);
        this.collateral = new HashMap<>();
    }

    @Override
    public double getLCRweight() {
        return Parameters.REPO_LCR;
    }

    @Override
    public String getName(Agent me) {
        if (me==assetParty) return "Reverse-repo to "+liabilityParty.getName();
        else return "Repo from "+assetParty.getName();
    }

    public void pledgeCollateral(CanBeCollateral asset, double quantity) {
        asset.encumber(quantity);

        if (collateral.containsKey(asset)) {
            collateral.put(asset, quantity + collateral.get(asset));
        } else {
            collateral.put(asset, quantity);
        }
    }

    private void unpledgeCollateral(CanBeCollateral asset, double quantity) {
        asset.unEncumber(quantity);
        assert(collateral.get(asset) >= quantity);
        collateral.put(asset, collateral.get(asset) - quantity);
    }

    public void marginCall() throws FailedMarginCallException {
        double currentValue = valueCollateralHaircutted();
        CanPledgeCollateral borrower = (CanPledgeCollateral) liabilityParty;

        if (currentValue < principal) { //TODO: finite precision

            if (principal - currentValue < borrower.getMaxUnencumberedHaircuttedCollateral()) {
                throw new FailedMarginCallException();
            }

            borrower.putMoreCollateral(principal - currentValue, this);

        } else if (currentValue > principal) {
             borrower.withdrawCollateral(principal - currentValue, this);
        }
    }

    private double valueCollateralHaircutted() {
        double value = 0;

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            Double quantity = entry.getValue();

            value += asset.getPrice() * quantity * (1.0 - asset.getHaircut());
        }

        return value;
    }

    public Set<Map.Entry<CanBeCollateral, Double>> getCollateral() {
        return collateral.entrySet();
    }

    public void unpledgeProportionally(double excessValue) {
        double totalValue = valueCollateralHaircutted();

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            double quantityToUnpledge = entry.getValue() * (1 - asset.getHaircut()) * excessValue / totalValue;
            unpledgeCollateral(asset, quantityToUnpledge);
        }
    }

    @Override
    public void liquidate() {
        super.liquidate();
        // When we liquidate a Repo, we must change the ownership of all the collateral and give it to the
        // asset party.

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            // 1. Take one type of collateral at a time
            CanBeCollateral asset = entry.getKey();
            double amountEncumbered = entry.getValue();

            // 2. Change the ownership of the asset
            ((Asset) asset).changeOwnership(assetParty, amountEncumbered);
        }
    }

    private HashMap<CanBeCollateral, Double> collateral;

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    @Override
    public double getValue() {
        return principal;
    }

}
