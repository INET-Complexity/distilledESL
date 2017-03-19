package contracts.obligations;

import agents.Agent;
import contracts.Contract;
import demos.BoEDemo;


public abstract class Obligation {
    protected double amount;
    private boolean fulfilled = false;
    private Agent from;
    private Agent to;
    private int timeToOpen;
    private int timeToPay;

    Obligation(Contract contract, double amount, int timeLeftToPay) {
        this.amount = amount;
        this.from = contract.getLiabilityParty();
        this.to = contract.getAssetParty();
        this.timeToPay = BoEDemo.getTime() + timeLeftToPay;
        this.timeToOpen = BoEDemo.getTime() + 1;

        assert(timeToPay >= timeToOpen);
    }

    public abstract void fulfil();

    public double getAmount() {
        return amount;
    }

    boolean isFulfilled() {
        return fulfilled;
    }

    boolean hasArrived() {
        return BoEDemo.getTime() == timeToOpen;
    }

    boolean isDue() {
        return BoEDemo.getTime() == timeToPay;
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

    public int getTimeToPay() {return timeToPay;}

}
