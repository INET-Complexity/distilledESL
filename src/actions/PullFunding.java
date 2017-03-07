package actions;

import agents.Agent;
import agents.Bank;
import contracts.Loan;

public class PullFunding extends Action {

    private Loan loan;
    public PullFunding(Loan loan) {
        this.loan = loan;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        Agent lender = loan.getAssetParty();
        Agent borrower = loan.getLiabilityParty();

        lender.pullFunding(getAmount(), loan);
        try {
            borrower.payLoan(getAmount(), loan);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update the loan
        loan.reducePrincipal(getAmount());
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

    public String getName() {
        return "PullFunding from "+loan.getLiabilityParty().getName();
    }
}
