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
    private Agent from;
    private Agent to;

    public Obligation(Contract contract, double amount, int timeLeftToPay) {
        this.contract = contract;
        this.amount = amount;
        this.timeLeftToPay = timeLeftToPay;
        this.from = contract.getAssetParty();
        this.to = contract.getLiabilityParty();
    }

    public void fulfil() {
        if (contract instanceof Loan) {
            ((Loan) contract).payLoan(amount);
            System.out.println(contract.getLiabilityParty().getName()+" has fulfilled an obligation to pay " +
                    contract.getAssetParty().getName()+
                    " an amount "+String.format("%.2f", amount)+".");
        } else if (contract instanceof Shares) {
            ((Shares) contract).cashIn(amount);
            System.out.println(contract.getLiabilityParty().getName()+" has fulfilled an obligation to redeem shares and pay " +
                    contract.getAssetParty().getName()+
                    " an amount "+String.format("%.2f", amount)+".");
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
        if (!arrived) {
            arrived = true;
            System.out.println("Obligation received from "+from.getName()+".\n");
        }
        else timeLeftToPay -= 1;
        //Todo: this is in timesteps. Might move to some other unit of time.
    }

    public Agent getFrom() {
        return from;
    }

    public Agent getTo() {
        return to;
    }
}
