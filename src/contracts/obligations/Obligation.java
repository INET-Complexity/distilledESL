package contracts.obligations;

import agents.Agent;
import contracts.Contract;


public abstract class Obligation {
    private Contract contract;
    protected double amount;
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

    public abstract void fulfil();

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

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }
}
