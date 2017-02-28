package agents;

import accounting.*;
import actions.Action;
import actions.LCR_Constraint;
import behaviours.BankBehaviour;
import actions.LeverageConstraint;
import actions.SellAsset;
import contracts.Asset;
import contracts.Contract;
import contracts.Loan;

import java.util.ArrayList;

/**
 * This class represents a simple bank with a single Book, called 'general Book'.
 *
 * Every Bank has a BankBehaviour.
 */
public class Bank extends Agent {

    private LeverageConstraint leverageConstraint;
    private LCR_Constraint lcr_constraint;
    private BankBehaviour behaviour;
    private Book mainBook;


    public Bank(String name) {
        super(name);
        mainBook = new Book(this);
    }



    @Override
    public void add(Contract contract) {
        if (contract.getAssetParty()==this) {
            // This contract is an asset for me.
            mainBook.addAsset(contract);
        } else if (contract.getLiabilityParty()==this) {
            // This contract is a liability for me
            mainBook.addLiability(contract);
        }
    }

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        return mainBook.getAvailableActions(this);
    }

    public void addCash(double amount) {
        mainBook.addCash(amount);}

    public Book getMainBook() {
        return mainBook;
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
        mainBook.updateAssetPrices();
    }

    public double getCash() {
        return mainBook.getCash();
    }

    public void printBalanceSheet() {
        System.out.println();
        System.out.println("Balance Sheet of "+getName());
        System.out.println("**************************");
        mainBook.printBalanceSheet();
        System.out.println("Leverage ratio: "+String.format("%.2f", 100*leverageConstraint.getLeverage()) + "%");
        System.out.println();
    }

    public void liquidateLoan(double initialValue, double valueFraction) {
        mainBook.liquidateLoan(initialValue, valueFraction);
    }

    //Todo: is this the best way to do this? This should really be in Behaviour
    public void raiseLiquidity(double liquidityNeeded) {
        ArrayList<Action> availableActions = getAvailableActions(this);

        double initialAssetHoldings = mainBook.getAssetValueOf(Asset.class);

        for (Action action : availableActions) {
            if (action instanceof SellAsset) {
                action.setAmount(action.getMax()*liquidityNeeded/initialAssetHoldings);
                action.print();
                action.perform();
            }
        }

    }

    @Override
    public void pullFunding(double amount) {
        mainBook.pullFunding(amount);
    }

    @Override
    public void payLoan(double amount)  {
        mainBook.payLiability(amount, Loan.class);
    }

    public double getAssetValue() {return mainBook.getAssetValue();}
    public double getLiabilityValue() {return mainBook.getLiabilityValue();}
    public double getEquityValue() {return mainBook.getEquityValue();}

}
