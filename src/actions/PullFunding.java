package actions;

import contracts.Loan;
import contracts.obligations.Obligation;
import contracts.obligations.PullFundingObligation;
import demos.Parameters;

public class PullFunding extends Action {

    private Loan loan;
    public PullFunding(Loan loan) {
        this.loan = loan;
        setAmount(0.0);
    }

    public Loan getLoan() {
        return loan;
    }

    @Override
    public void perform() {
        loan.increaseFundingPulled(getAmount());

        if (loan.getLiabilityParty()==null || !Parameters.FUNDING_CONTAGION_HEDGEFUND) {
            // If there's no counter-party OR if there's no funding contagion, the payment can happen instantaneously
            loan.payLoan(getAmount());
        } else {
            // If there is a counter-party AND we have funding contagion, we must send a Obligation.
            Obligation obligation = new PullFundingObligation(loan, getAmount(), Parameters.TIMESTEPS_TO_PAY);

            loan.getAssetParty().addToOutbox(obligation);
            loan.getLiabilityParty().addToInbox(obligation);
        }
    }

    @Override
    public double getMax() {
        return (loan.getValue() - loan.getFundingAlreadyPulled());
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