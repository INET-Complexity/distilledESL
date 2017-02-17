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
 * This class represents a simple bank with a single Ledger, called 'general Ledger'.
 *
 * Every Bank has a BankBehaviour.
 */
public class Bank extends Agent {

    private LeverageConstraint leverageConstraint;
    private LCR_Constraint lcr_constraint;
    private BankBehaviour behaviour;
    private Ledger generalLedger;


    public Bank(String name) {
        super(name);
        generalLedger = new Ledger(this);

        // TODO: We need a better way to initialise the bank accounts!
        // Add the standard accounts to the bank here

        generalLedger.addCashAccount(new Account("cash", AccountType.ASSET,0.0));
        generalLedger.addAccount(new Account("assets", AccountType.ASSET,0.0), Asset.class);
        generalLedger.addAccount(new Account("loans (lending)", AccountType.ASSET, 0.0), Loan.class);
        generalLedger.addAccount(new Account("loans (borrowing)", AccountType.LIABILITY, 0.0), Loan.class);
        generalLedger.addEquityAccount(new Account("equity", AccountType.EQUITY,0.0));

    }



    @Override
    public void add(Contract contract) {
        if (contract.getAssetParty()==this) {
            // This contract is an asset for me.
            generalLedger.addAsset(contract);
        } else if (contract.getLiabilityParty()==this) {
            // This contract is a liability for me
            generalLedger.addLiability(contract);
        }
    }

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        return generalLedger.getAvailableActions(this);
    }

    public void addCash(double amount) {generalLedger.addCash(amount);}

    public Ledger getGeneralLedger() {
        return generalLedger;
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
        generalLedger.updateAssetPrices();
    }

    public double getCash() {
        return generalLedger.getCash();
    }

    public void printBalanceSheet() {
        System.out.println();
        System.out.println("Balance Sheet of "+getName());
        System.out.println("**************************");
        generalLedger.printBalanceSheet();
        System.out.println("Leverage ratio: "+String.format("%.2f", 100*leverageConstraint.getLeverage()) + "%");
        System.out.println();
    }

    public void liquidateLoan(double initialValue, double valueFraction) {
        generalLedger.liquidateLoan(initialValue, valueFraction);
    }

    public void raiseLiquidity(double liquidityNeeded) {
        ArrayList<Action> availableActions = getAvailableActions(this);

        double initialAssetHoldings = generalLedger.getAssetAccountFor(Asset.class).getTotal();

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
        generalLedger.pullFunding(amount);
    }

    @Override
    public void payLoan(double amount) throws Exception {
        generalLedger.payLoan(amount);
    }

    public double getAssetValue() {return generalLedger.getAssetValue();}
    public double getLiabilityValue() {return generalLedger.getLiabilityValue();}
    public double getEquityValue() {return generalLedger.getEquityValue();}

}
