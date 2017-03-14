package agents;

import contracts.Loan;

public class Request {
    private Loan loan;
    private double amount;
    private boolean fulfilled = false;
    private int timeLeftToPay;

    public Request(Loan loan, double amount, int timeLeftToPay) {
        this.loan = loan;
        this.amount = amount;
        this.timeLeftToPay = timeLeftToPay;
    }

    public void fulfil() {
        loan.payLoan(amount);
        fulfilled = true;
    }

    public Loan getLoan() {
        return loan;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public int getTimeLeftToPay() {
        return timeLeftToPay;
    }

    public boolean isDue() {
        return timeLeftToPay==0;
    }

    public void tick() {
        timeLeftToPay -= 1;
        //Todo: this is in timesteps. Might move to some other unit of time.
    }
}
