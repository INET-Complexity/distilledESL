package contracts.obligations;

import agents.Agent;
import contracts.Contract;


public abstract class Obligation {
    protected double amount;
    private boolean fulfilled = false;
    private int timeLeftToPay;
    private Agent from;
    private Agent to;

    Obligation(Contract contract, double amount, int timeLeftToPay) {
        this.amount = amount;
        this.timeLeftToPay = timeLeftToPay;
        this.from = contract.getAssetParty();
        this.to = contract.getLiabilityParty();
    }

    public abstract void fulfil();

    public double getAmount() {
        return amount;
    }

    boolean isFulfilled() {
        return fulfilled;
    }

    boolean isDue() {
        return timeLeftToPay==0;
    }


    void tick() {
        timeLeftToPay -= 1;
        //Todo: this is in timesteps. Might move to some other unit of time.
    }

    public Agent getFrom() {
        return from;
    }

    public Agent getTo() {
        return to;
    }

    void setFulfilled() {
        this.fulfilled = true;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

}
