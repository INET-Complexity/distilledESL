package doubleEntryComponents;

import doubleEntry.*;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.actions.Behaviour;
import doubleEntryComponents.actions.LeverageConstraint;
import doubleEntryComponents.contracts.Asset;
import doubleEntryComponents.contracts.Contract;
import doubleEntryComponents.contracts.Loan;

import java.util.ArrayList;

public class Bank extends Agent {

    private LeverageConstraint leverageConstraint;
    private Behaviour behaviour;

    public Bank(String name) {
        super(name);
        generalLedger = new Ledger();

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

    private Ledger generalLedger;

    public Ledger getGeneralLedger() {
        return generalLedger;
    }

    public void setLeverageConstraint(LeverageConstraint leverageConstraint) {
        this.leverageConstraint = leverageConstraint;
    }

    public LeverageConstraint getLeverageConstraint() {
        return leverageConstraint;
    }

    public void setBehaviour(Behaviour behaviour) {
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
        generalLedger.printBalanceSheet();
    }

}
