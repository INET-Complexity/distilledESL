package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;
import doubleEntryComponents.contracts.Loan;

public class PullFunding extends Action {

    private Loan loan;
    public PullFunding(Loan loan) {
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
        try {
            borrower.getGeneralLedger().payLoan(getAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public double getMax() {
        //Todo: we need to check that the other party can afford to pay the loan!!
        return loan.getValue();
    }

    @Override
    public void print() {
        System.out.println("PullFunding action by "+loan.getAssetParty().getName()+" -> amount "
            + String.format( "%.2f", getAmount()) +", borrower is "+loan.getLiabilityParty().getName());
    }
}
