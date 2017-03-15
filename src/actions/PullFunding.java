package actions;

import agents.Request;
import contracts.Loan;

public class PullFunding extends Action {

    private Loan loan;
    public PullFunding(Loan loan) {
        this.loan = loan;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        if (loan.getLiabilityParty()==null) {
            // If there's no counter-party, the payment can happen instantaneously
            loan.payLoan(getAmount());
        } else {
            // If there is a counter-party, we must send a Request.
            Request request = new Request(loan, getAmount(), 2);
            //Todo: how many timesteps do we allow the counterparty to raise the liquidity to pay?
            loan.getAssetParty().addToOutbox(request);
            loan.getLiabilityParty().addToInbox(request);
            loan.setCancelled(true);
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
