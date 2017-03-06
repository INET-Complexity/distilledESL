package actions;

import agents.Bank;
import contracts.Loan;

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

        try {borrower.getMainLedger().payLiability(getAmount(), loan);}
        catch (Exception e) {
            e.printStackTrace();
        }

        if (loan.getAssetParty()!= null) {
            Bank lender = (Bank) loan.getAssetParty();
            lender.getMainLedger().pullFunding(getAmount(), loan);
        }

        loan.reducePrincipal(getAmount());

    }

    public double getMax() {
        return loan.getValue();
    }

    @Override
    public void print() {
        System.out.println("PayLoan action by "+loan.getLiabilityParty().getName()+" -> amount: "+String.format( "%.2f", getAmount()));
    }

    public String getName() {
        return "PayLoan";
    }

    public Loan getLoan() {
        return loan;
    }
}
