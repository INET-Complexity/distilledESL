package agents;

import contracts.Loan;

public class Request {
    private Loan loan;
    private double amount;
    private boolean fulfilled = false;

    public Request(Loan loan, double amount) {
        this.loan = loan;
        this.amount = amount;
    }

    public void fulfil() {
        loan.payLoan(amount);
        fulfilled = true;
    }

    public boolean fulfilled() {return fulfilled;}
}
