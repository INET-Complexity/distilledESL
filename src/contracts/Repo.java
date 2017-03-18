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

    private double cashCollateral;

    public Repo(Agent assetParty, Agent liabilityParty, double principal) {
        super(assetParty, liabilityParty, principal);
        this.collateral = new HashMap<>();
        this.cashCollateral = 0.0;
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

    public void pledgeCashCollateral(double amount) {
        cashCollateral += amount;
    }

    private void unpledgeCollateral(CanBeCollateral asset, double quantity) {
        asset.unEncumber(quantity);
        assert(collateral.get(asset) >= quantity);
        collateral.put(asset, collateral.get(asset) - quantity);
    }

    private void unpledgeCashCollateral(double amount) {
        assert(cashCollateral > amount);

    }

    public void marginCall() throws FailedMarginCallException {
        double currentValue = valueCollateralHaircutted();

        CanPledgeCollateral borrower = (CanPledgeCollateral) liabilityParty;

        if (currentValue < principal) { //TODO: finite precision

            if ((principal - currentValue) > borrower.getMaxUnencumberedHaircuttedCollateral()) {
                System.out.println("The margin call on Repo"+getName(liabilityParty)+" failed." +
                        " The value of the collateral was "+currentValue+",\n but the principal of the repo is "+principal +
                        " and I only have a total extra collateral of "+borrower.getMaxUnencumberedHaircuttedCollateral());

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

        value += cashCollateral;

        return value;
    }

    public Set<Map.Entry<CanBeCollateral, Double>> getCollateral() {
        return collateral.entrySet();
    }

    public void unpledgeProportionally(double excessValue) {
        double totalValue = valueCollateralHaircutted();
        double unpledgedSoFar = 0.0;

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            double quantityToUnpledge = entry.getValue() * (1 - asset.getHaircut()) * excessValue / totalValue;
            unpledgeCollateral(asset, quantityToUnpledge);
            unpledgedSoFar += quantityToUnpledge;
        }

        unpledgeCashCollateral(excessValue - unpledgedSoFar);

    }

    @Override
    public void liquidate() {
        // When we liquidate a Repo, we must change the ownership of all the collateral and give it to the
        // asset party.

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            // 1. Take one type of collateral at a time
            CanBeCollateral asset = entry.getKey();
            double amountEncumbered = entry.getValue();

            // 2. Change the ownership of the asset
            ((Asset) asset).changeOwnership(assetParty, amountEncumbered);

            // 3. Reduce the value of this repo to zero.
            assetParty.devalueAsset(this, principal);
            liabilityParty.devalueLiability(this, principal);

            if (Parameters.FIRESALES_UPON_DEFAULT) {
                ((Asset) asset).putForSale(((Asset) asset).getQuantity());
            }
        }

        principal = 0;

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
    public double getValue(Agent me) {
        return principal;
    }

    @Override
    public double getRWAweight() {
        return 0.0;
    }

    public void printCollateral() {
        System.out.println("Collateral of "+getName(liabilityParty));
        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            Double quantity = entry.getValue();

            System.out.println( ((Contract) asset).getName(liabilityParty)+" for an amount "+quantity +
            ", price "+asset.getPrice()+" and haircut "+asset.getHaircut());
        }
    }
}
