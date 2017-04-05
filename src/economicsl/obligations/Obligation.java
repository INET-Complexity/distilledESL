package economicsl.obligations;

import economicsl.Agent;
import contracts.Contract;
import demos.Model;


public abstract class Obligation {
    protected double amount;
    private boolean fulfilled = false;
    private Agent from;
    private Agent to;
    private int timeToOpen;
    private int timeToPay;
    private int timeToReceive;

    public Obligation(Contract contract, double amount, int timeLeftToPay) {
        this.amount = amount;

        this.from = contract.getLiabilityParty();
        this.to = contract.getAssetParty();

        this.timeToOpen = Model.getTime() + 1;
        this.timeToPay = Model.getTime() + timeLeftToPay;
        this.timeToReceive = timeToPay + 1;

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
        return Model.getTime() == timeToOpen;
    }

    boolean isDue() {
        return Model.getTime() == timeToPay;
    }

    public Agent getFrom() {
        return from;
    }

    public Agent getTo() {
        return to;
    }

    public void setFulfilled() {
        this.fulfilled = true;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTimeToPay() {return timeToPay;}

    public int getTimeToReceive() { return timeToReceive;}

    public void printObligation() {
        System.out.println("Obligation from "+getFrom().getName()+" to pay "+getTo().getName() +
        " an amount "+getAmount()+" on timestep "+getTimeToPay()+" to arrive by timestep "+getTimeToReceive());
    }

}
