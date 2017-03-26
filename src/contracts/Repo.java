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
    private HashMap<CanBeCollateral, Double> collateral;

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
        if (me==assetParty) return (liabilityParty!=null) ?
                "Reverse-repo to "+liabilityParty.getName() : "Reverse-repo to uninitialised Agent";
        //Todo: deal with null parties?
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
        liabilityParty.encumberCash(amount);
    }

    private void unpledgeCashCollateral(double amount) {
        assert(cashCollateral > amount);
        cashCollateral -= amount;
        liabilityParty.unencumberCash(amount);

    }

    private void unpledgeCollateral(CanBeCollateral asset, double quantity) {
        asset.unEncumber(quantity);
        assert(collateral.get(asset) >= quantity);
        collateral.put(asset, collateral.get(asset) - quantity);
    }


    public void marginCall() throws FailedMarginCallException {
        double currentValue = valueCollateralHaircutted();
        double valueNeeded = principal - getFundingAlreadyPulled();

        CanPledgeCollateral borrower = (CanPledgeCollateral) liabilityParty;

        if (currentValue < valueNeeded) {
            System.out.println("This Repo is short of collateral for an amount "+(valueNeeded - currentValue));

            if ((valueNeeded - currentValue) > borrower.getMaxUnencumberedHaircuttedCollateral()) {
                System.out.println("The margin call on Repo"+getName(liabilityParty)+" failed." +
                        " The value of the collateral was "+currentValue+",\n but the principal of the repo is "+principal +
                        ", of which "+getFundingAlreadyPulled()+" has already been pulled, and I only have a total extra collateral of "+borrower.getMaxUnencumberedHaircuttedCollateral());

                throw new FailedMarginCallException();
            }

            borrower.putMoreCollateral(valueNeeded - currentValue, this);

        } else if (currentValue > valueNeeded) {
            borrower.withdrawCollateral(currentValue - valueNeeded, this);
        }
    }

    public void marginCallBeforeLiquidating() {
        double currentValue = valueCollateralHaircutted();
        double valueNeeded = principal;

        CanPledgeCollateral borrower = (CanPledgeCollateral) liabilityParty;

        if (currentValue < valueNeeded) {
            System.out.println("We are liquidating a repo and we need to encumber more assets for value "+(valueNeeded - currentValue));

            double amountToEncumber = valueNeeded - currentValue;
            if ((valueNeeded - currentValue) > borrower.getMaxUnencumberedHaircuttedCollateral()) {
                System.out.println("We do not have enough assets to encumber up to the original value of the repo. We will encumber as much as possible.");
                amountToEncumber = borrower.getMaxUnencumberedHaircuttedCollateral();
            }

            borrower.putMoreCollateral(amountToEncumber, this);

        } else if (currentValue > valueNeeded) {
            System.out.println("Strange. We're liquidating a repo which has too much collateral pledged to it (!)");
            borrower.withdrawCollateral(currentValue - valueNeeded, this);
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
        double initialCollateralValue = valueCollateralHaircutted();
//        double haircuttedValueUnpledgedSoFar = 0.0;

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            double quantityToUnpledge = entry.getValue() * excessValue / initialCollateralValue;
            unpledgeCollateral(asset, quantityToUnpledge);
//            haircuttedValueUnpledgedSoFar += quantityToUnpledge * asset.getPrice() * (1.0 - asset.getHaircut());
        }

        double currentCollateralValue = valueCollateralHaircutted();

        unpledgeCashCollateral(excessValue - (initialCollateralValue - currentCollateralValue));

    }

    @Override
    public void liquidate() {
        // When we liquidate a Repo, we must:
        // - re-run the margin call to reintroduce as much collateral as possible into the repo.
        // - change the ownership of all the collateral and give it to the
        // asset party.

        // re-run the margin call with fundingAlreadyPulled set to zero.
        marginCallBeforeLiquidating();

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            // 1. Take one type of collateral at a time
            CanBeCollateral asset = entry.getKey();
            double amountEncumbered = entry.getValue();

            // 2. Change the ownership of the asset
            Asset newAsset = ((Asset) asset).changeOwnership(assetParty, amountEncumbered);

            // 3. Reduce the value of this repo to zero.
            assetParty.devalueAsset(this, principal);
            liabilityParty.devalueLiability(this, principal);

            if (Parameters.FIRESALES_UPON_DEFAULT) {
                newAsset.putForSale(newAsset.getQuantity());
            }
        }

        principal = 0;

    }

    @Override
    public double getRWAweight() {
        return 0.0;
    }

    public void printCollateral() {
        System.out.println("\nCollateral of "+getName(liabilityParty));
        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            Double quantity = entry.getValue();

            System.out.println( ((Contract) asset).getName(liabilityParty)+" for an amount "+quantity +
            ", price "+asset.getPrice()+" and haircut "+asset.getHaircut());
        }
        System.out.println("Cash collateral is "+cashCollateral);
        System.out.println("Principal of the Repo is "+principal);
        System.out.println("Amount already pulled is "+getFundingAlreadyPulled());
        System.out.println("Amount of collateral needed is "+(principal - getFundingAlreadyPulled()));
        System.out.printf("Current value of collateral is "+valueCollateralHaircutted());
    }



}
