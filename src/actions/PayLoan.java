package actions;

import contracts.Loan;

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
        loan.payLoan(getAmount());
    }

    public double getMax() {
        return loan.getValue();
    }

    @Override
    public void print() {
        System.out.println("Pay Loan action by "+loan.getLiabilityParty().getName()+" -> amount: "+String.format( "%.2f", getAmount()));
    }

    public String getName() {
        if (loan.getAssetParty()==null) {
            return "Pay Loan to unspecified lender [max: "+getMax()+"]";
        } else {
            return "Pay Loan to "+loan.getAssetParty().getName()+" [max: "+getMax()+"]";
        }
    }

    public Loan getLoan() {
        return loan;
    }
}