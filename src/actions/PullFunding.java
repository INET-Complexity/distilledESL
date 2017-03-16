package actions;

import agents.Obligation;
import contracts.Loan;
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
        if (loan.getLiabilityParty()==null || !Parameters.FUNDING_CONTAGION) {
            // If there's no counter-party OR if there's no funding contagion, the payment can happen instantaneously
            loan.getAssetParty().pullFunding(getAmount(), loan);
        } else {
            // If there is a counter-party AND we have funding contagion, we must send a Obligation.
            Obligation obligation = new Obligation(loan, getAmount(), 2);
            //Todo: how many timesteps do we allow the counterparty to raise the liquidity to pay?
            loan.getAssetParty().addToOutbox(obligation);
            loan.getLiabilityParty().addToInbox(obligation);
        }
    }

    @Override
    public double getMax() {
        return loan.getValue();
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
