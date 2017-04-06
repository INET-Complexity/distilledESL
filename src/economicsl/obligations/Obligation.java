package economicsl.obligations;

import economicsl.Agent;
import economicsl.Contract;
import economicsl.Simulation;


public abstract class Obligation {
    private final Simulation simulation;
    protected double amount;
    private boolean fulfilled = false;
    private Agent from;
    private Agent to;
    private int timeToOpen;
    private int timeToPay;
    private int timeToReceive;

    public Obligation(Contract contract, double amount, int timeLeftToPay, Simulation simulation) {
        this.amount = amount;

        this.from = contract.getLiabilityParty();
        this.to = contract.getAssetParty();

        this.timeToOpen = simulation.getTime() + 1;
        this.timeToPay = simulation.getTime() + timeLeftToPay;
        this.timeToReceive = timeToPay + 1;
        this.simulation = simulation;

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
        return this.simulation.getTime() == timeToOpen;
    }

    boolean isDue() {
        return this.simulation.getTime() == timeToPay;
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
