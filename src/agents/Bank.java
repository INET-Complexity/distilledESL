package agents;

import accounting.*;
import actions.Action;
import actions.LCR_Constraint;
import behaviours.BankBehaviour;
import actions.LeverageConstraint;
import actions.SellAsset;
import contracts.*;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class represents a simple bank with a single Ledger, called 'general Ledger'.
 *
 * Every Bank has a BankBehaviour.
 */
public class Bank extends Agent implements CanPledgeCollateral {

    private LeverageConstraint leverageConstraint;
    private LCR_Constraint lcr_constraint;
    private BankBehaviour behaviour;
    private Ledger mainLedger;


    public Bank(String name) {
        super(name);
        mainLedger = new Ledger(this);
    }



    @Override
    public void add(Contract contract) {
        if (contract.getAssetParty()==this) {
            // This contract is an asset for me.
            mainLedger.addAsset(contract);
        } else if (contract.getLiabilityParty()==this) {
            // This contract is a liability for me
            mainLedger.addLiability(contract);
        }
    }

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        return mainLedger.getAvailableActions(this);
    }

    public void addCash(double amount) {
        mainLedger.addCash(amount);}

    public Ledger getMainLedger() {
        return mainLedger;
    }

    public void setLeverageConstraint(LeverageConstraint leverageConstraint) {
        this.leverageConstraint = leverageConstraint;
    }

    public LeverageConstraint getLeverageConstraint() {
        return leverageConstraint;
    }

    public LCR_Constraint getLCR_constraint() {
        return lcr_constraint;
    }

    public void setLCR_constraint(LCR_Constraint lcr_constraint) {
        this.lcr_constraint = lcr_constraint;
    }

    public void setBehaviour(BankBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void act() {
        behaviour.act();
    }

    public void updateAssetPrices() {
        mainLedger.updateAssetPrices();
    }

    public double getCash() {
        return mainLedger.getCash();
    }

    public void printBalanceSheet() {
        System.out.println();
        System.out.println("Balance Sheet of "+getName());
        System.out.println("**************************");
        mainLedger.printBalanceSheet();
        System.out.println("Leverage ratio: "+String.format("%.2f", 100*leverageConstraint.getLeverage()) + "%");
        System.out.println();
    }

    public void liquidateLoan(double initialValue, double valueFraction, Contract loan) {
        mainLedger.liquidateLoan(initialValue, valueFraction, loan);
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
    public void pullFunding(double amount, Contract loan) {
        mainLedger.pullFunding(amount, loan);
    }

    @Override
    public void payLoan(double amount, Contract loan)  {
        mainLedger.payLiability(amount, loan);
    }

    public double getAssetValue() {return mainLedger.getAssetValue();}
    public double getLiabilityValue() {return mainLedger.getLiabilityValue();}
    public double getEquityValue() {return mainLedger.getEquityValue();}


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
}
