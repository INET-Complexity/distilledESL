package agents;

import contracts.Contract;
import contracts.Loan;
import contracts.Shares;

public class Obligation {
    private Contract contract;
    private double amount;
    private boolean fulfilled = false;
    private int timeLeftToPay;
    private boolean arrived = false;

    public Obligation(Contract contract, double amount, int timeLeftToPay) {
        this.contract = contract;
        this.amount = amount;
        this.timeLeftToPay = timeLeftToPay;
    }

    public void fulfil() {
        if (contract instanceof Loan) {
            ((Loan) contract).payLoan(amount);
        } else if (contract instanceof Shares) {
            ((Shares) contract).redeem((int) amount);
        }
        fulfilled = true;
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

    public boolean hasArrived() { return arrived;}

    public void tick() {
        if (!arrived) arrived = true;
        else timeLeftToPay -= 1;
        //Todo: this is in timesteps. Might move to some other unit of time.
    }
}
