package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;
import doubleEntryComponents.contracts.Loan;

public class CancelLoan extends Action {

    private Loan loan;
    public CancelLoan(Loan loan) {
        this.loan = loan;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        //Todo: the asset party has to be a bank!
        Bank lender = (Bank) loan.getAssetParty();
        Bank borrower = (Bank) loan.getLiabilityParty();


        //Todo: we are accessing the bank's ledger!
        lender.getGeneralLedger().pullFunding(getAmount());
        borrower.getGeneralLedger().payLoan(getAmount());

    }

    @Override
    public double getMax() {
        //Todo: we need to check that the other party can afford to pay the loan!!
        return loan.getValue();
    }

    @Override
    public void print() {
        System.out.println("CancelLoan action by "+loan.getAssetParty().getName()+" -> amount "
            +getAmount()+", borrower is "+loan.getLiabilityParty().getName());
    }
}
