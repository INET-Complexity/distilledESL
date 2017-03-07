package agents;

import accounting.Ledger;
import actions.Action;
import behaviours.Behaviour;
import contracts.Contract;

import java.util.ArrayList;

public abstract class Agent {
    private String name;
    Ledger mainLedger;
    Behaviour behaviour;

    public Agent(String name) {
        this.name = name;
        mainLedger = new Ledger(this);
    }

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }


    public String getName() {
        return name;
    }

    public abstract void raiseLiquidity(double amount);

    public void pullFunding(double amount, Contract loan) {
        mainLedger.pullFunding(amount, loan);
    }

    public void payLoan(double amount, Contract loan)  {
        //Todo: What do we do if we can't pay??!! At the moment I'm forced to raise liquidity immediately
        if (getCash() < amount) {
            System.out.println();
            System.out.println("***");
            System.out.println(getName()+" must raise liquidity immediately.");
            raiseLiquidity(amount * (1 - getCash() / getAssetValue()));
            System.out.println("***");
            System.out.println();
        }

        mainLedger.payLiability(amount, loan);
    }

    public void add(Contract contract) {
        if (contract.getAssetParty()==this) {
            // This contract is an asset for me.
            mainLedger.addAsset(contract);
        } else if (contract.getLiabilityParty()==this) {
            // This contract is a liability for me
            mainLedger.addLiability(contract);
        }
    }

    public void addCash(double amount) {
        mainLedger.addCash(amount);}

    public double getCash() {
        return mainLedger.getCash();
    }


    public ArrayList<Action> getAvailableActions(Agent me) {
        return mainLedger.getAvailableActions(this);
    }

    public Ledger getMainLedger() {
        return mainLedger;
    }

    public void act() {
        behaviour.act();
    }

    public void updateAssetPrices() {
        mainLedger.updateAssetPrices();
    }

    public double getLeverage() {
        return (1.0 * getEquityValue() / getAssetValue());
    }

    public void liquidateLoan(double initialValue, double valueFraction, Contract loan) {
        mainLedger.liquidateLoan(initialValue, valueFraction, loan);
    }

    public double getAssetValue() {return mainLedger.getAssetValue();}
    public double getLiabilityValue() {return mainLedger.getLiabilityValue();}
    public double getEquityValue() {return mainLedger.getEquityValue();}


    public void printBalanceSheet() {
        System.out.println();
        System.out.println("Balance Sheet of "+getName());
        System.out.println("**************************");
        mainLedger.printBalanceSheet();
        System.out.println("Leverage ratio: "+String.format("%.2f", 100*getLeverage()) + "%");
        System.out.println();
    }


}
