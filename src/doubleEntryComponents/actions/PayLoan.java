package doubleEntryComponents.actions;

import doubleEntryComponents.Agent;
import doubleEntryComponents.Bank;
import doubleEntryComponents.contracts.Loan;

import static java.lang.Math.min;

/**
 * the Payloan action represents the chance to pay back a loan that the agent has on its liability side.
 *
 * Its perform function takes one parameter: amount.
 */
public class PayLoan extends Action {

    private Loan loan;

    public PayLoan(Loan loan) {
        this.loan = loan;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        //if (amount == 0.0) {throw new UnderspecifiedActionException();}

        Bank borrower = (Bank) loan.getLiabilityParty();

        try {borrower.getGeneralLedger().payLoan(getAmount());}
        catch (Exception e) {
            e.printStackTrace();
        }

        if (loan.getAssetParty()!= null) {
            Bank lender = (Bank) loan.getAssetParty();
            lender.getGeneralLedger().pullFunding(getAmount());
        }

        loan.reducePrincipal(getAmount());

    }

    public double getMax() {
        // The maximum action possible is the min of:
        // - the loan principal
        // - the agent's cash
        return min(((Bank) loan.getLiabilityParty()).getCash(), loan.getValue());
    }

    @Override
    public void print() {
        System.out.println("PayLoan action by "+loan.getLiabilityParty().getName()+" -> amount: "+String.format( "%.2f", getAmount()));
    }

    public Loan getLoan() {
        return loan;
    }
}
