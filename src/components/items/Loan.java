package components.items;

import ESL.agent.Agent;
import ESL.contract.MasonScheduledContracts;
import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.contract.obligation.ScheduledObligation;
import ESL.inventory.Good;
import sim.engine.SimState;

/**
 * A standard loan.
 *
 * @author rafa
 */
public class Loan extends MasonScheduledContracts implements Collateral {
    /**
     * Constructor allowing for an issue price different to the face value.
     * @param name name of the bond
     * @param state the SimState (the main simulation)
     * @param handler a contract handler that will deal with the obligations
     * @param issuer the party that issues the bond, i.e. the borrower
     * @param holder the party that holds the bond, i.e. the lender
     * @param principal the principal
     * @param rate the fixed rate for the coupons, with respect to the FACE VALUE (not the issue price)
     * @param couponFrequency the time in timesteps (for the moment) between subsequent coupon payments
     */
    public Loan(String name, SimState state, ContractHandler handler, Agent issuer, Agent holder, Double principal,
                Double rate, Double couponFrequency) {

        super(name, state, handler);

        currentState = State.PRINCIPAL;
        this.issuer  = issuer;
        this.holder = holder;
        this.principal = principal;
        this.rate=rate;
    }

    public void start(SimState state) {
        this.scheduleEvent(requestNextObligation(state), state);
    }

    @Override
    public ScheduledObligation requestNextObligation(SimState state) {

        Obligation o = null;
        Double time = null;

        switch (this.currentState) {

            case PRINCIPAL:
                o = new Obligation(this.issuer, this.holder, new GBP(principal));
                time = state.schedule.getSteps()+1.0;
                break;
            case MATURED:
                o = new Obligation(this.holder, this.issuer, new GBP(principal*(1.0+rate)));
                time = state.schedule.getSteps()+1.0;
                break;
        }

        return (new ScheduledObligation(o, time));
    }

    @Override
    public void handleResponse(ObligationResponse response) {

        printObligation(response.getObligation());

        // switch state to DEFAULT if the response is false
        if (!response.getFilled()) {
            this.currentState = State.DEFAULT;
            return;
        }

        // change the state based on the response to the previous obligation
        switch (this.currentState) {

            case PRINCIPAL:
                this.currentState = State.MATURED;
                break;

            case MATURED:
                this.currentState = State.TERMINATED;
                break;

            case TERMINATED:
                // if the bond is terminated and I'm handling a response, something's gone wrong
                System.out.println("Strange: I'm a terminated bond and I'm handling a response?");
                break;

        }

    }

    private void printObligation(Obligation o) {

        if (o == null) {
            return;
        }
        Agent from = o.getFrom();
        Agent to = o.getTo();
        String what = o.getWhat().getName();
        Double quantity = 1.0;

        if (o.getWhat() instanceof Good) {
            quantity = ((Good) o.getWhat()).getQuantity();
        }
        System.out.println("The current state is: " + this.currentState + ". Therefore, " + from.getName() + " gave "
                + to.getName() + " " + quantity + " of " + what);
        System.out.println("Bank " + from.getName() + " has £" + from.getInventory().getAllGoodEntries().get("GBP"));
        System.out.println("Bank " + to.getName() + " has £" + to.getInventory().getAllGoodEntries().get("GBP"));

    }

    @Override
    public void setEncumbered() {
        if (this.encumbered) {
            System.out.println("Strange: I'm setting this as encumbered but it already is.");
        }
        this.encumbered=true;
    }

    @Override
    public void setUnencumbered() {
        this.encumbered=false;
    }

    @Override
    public boolean isEncumbered() {
        return this.encumbered;
    }

    private enum State {
        PRINCIPAL, DEFAULT, MATURED, TERMINATED
    }

    private State currentState;
    private Agent issuer;
    private Agent holder;
    private Double principal;
    private Double rate;

    private Boolean encumbered;
}


