package contracts.obligations;

import contracts.Loan;

public class PullFundingObligation extends Obligation {
    private Loan loan;

    public PullFundingObligation(Loan loan, double amount, int timeLeftToPay) {
        super(loan, amount, timeLeftToPay);
        this.loan = loan;

    }

    @Override
    public void fulfil() {
        loan.payLoan(amount);
        System.out.println(loan.getLiabilityParty().getName()+" has fulfilled an obligation to pay " +
                loan.getAssetParty().getName()+
                " an amount "+String.format("%.2f", amount)+".");

        setFulfilled();
    }
}
