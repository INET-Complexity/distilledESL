package actions;

import agents.Agent;
import contracts.FailedMarginCallException;
import contracts.Loan;
import contracts.Repo;

/**
 * the Payloan action represents the chance to pay back a loan that the agent has on its liability side.
 *
 * Its perform function takes one parameter: amount.
 */
public class PayLoan extends Action {

    private Loan loan;

    public PayLoan(Agent me, Loan loan) {
        super(me);
        this.loan = loan;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        super.perform();
        loan.payLoan(getAmount());
        if (loan instanceof Repo) {
            try {
                ((Repo) loan).marginCall();
            } catch (FailedMarginCallException e) {
                //Todo
            }
        }
    }

    public double getMax() {
        return loan.getValue(null) - loan.getFundingAlreadyPulled();
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