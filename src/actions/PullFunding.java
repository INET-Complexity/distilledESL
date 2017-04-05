package actions;

import economicsl.Agent;
import contracts.FailedMarginCallException;
import contracts.Loan;
import contracts.Repo;
import economicsl.obligations.Obligation;
import contracts.obligations.PullFundingObligation;
import demos.Parameters;

public class PullFunding extends Action {

    private Loan loan;
    public PullFunding(Agent me, Loan loan) {
        super(me);
        this.loan = loan;
        setAmount(0.0);
    }

    public Loan getLoan() {
        return loan;
    }

    @Override
    public void perform() {
        super.perform();
        loan.increaseFundingPulled(getAmount());

        if (loan.getLiabilityParty()==null) {
            // If there's no counter-party the payment can happen instantaneously
            loan.payLoan(getAmount());
        } else {
            // If there is a counter-party AND we have funding contagion, we must send a Obligation.
            Obligation obligation = new PullFundingObligation(loan, getAmount(), Parameters.TIMESTEPS_TO_PAY);
            loan.getAssetParty().sendObligation(loan.getLiabilityParty(), obligation);
            if (loan instanceof Repo) {
                try {
                    System.out.println("I've pulled funding from a repo. My counterparty will perform a margin call to free up assets.");
                    ((Repo) loan).marginCall();
                } catch (FailedMarginCallException e) {
                    System.out.println("Strange! a margin call failed while the funding was being pulled.");
                    System.exit(-1);
                }
            }

        }
    }

    @Override
    public double getMax() {
        return (loan.getValue(null) - loan.getFundingAlreadyPulled());
    }

    @Override
    public void print() {
        System.out.println("Pull Funding action by "+loan.getAssetParty().getName()+" -> amount "
            + String.format( "%.2f", getAmount()) +", borrower is "+loan.getLiabilityParty().getName());
    }

    public String getName() {
        return "Pull Funding from "+loan.getLiabilityParty().getName()+" [max: "+getMax()+"]";
    }
}