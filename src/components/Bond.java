package components;

import ESL.agent.Agent;
import ESL.contract.MasonScheduledContracts;
import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.contract.obligation.ScheduledObligation;
import ESL.inventory.Good;
import sim.engine.SimState;

public class Bond extends MasonScheduledContracts {
    private State currentState;
    private Agent issuer;
    private Agent holder;
    private Double faceValue;
    private Double issuePrice;
    private Double rate;
    private Integer numCoupons;
    private Double couponFrequency;

    public Bond(String name, SimState state, ContractHandler handler, Agent issuer, Agent holder, Double faceValue,
                     Double issuePrice, Double rate, Integer numCoupons, Double couponFrequency) {

        super(name, state, handler);

        currentState = State.PRINCIPAL;
        this.issuer  = issuer;
        this.holder = holder;
        this.faceValue = faceValue;
        this.issuePrice = issuePrice;
        this.rate=rate;
        this.numCoupons=numCoupons;
        this.couponFrequency=couponFrequency;
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
                o = new Obligation(this.issuer, this.holder, new GBP(issuePrice));
                time = state.schedule.getSteps()+1.0;
                break;
            case COUPON:
                o = new Obligation(this.holder, this.issuer, new GBP(faceValue*this.rate));
                time = state.schedule.getSteps()+couponFrequency;
                break;
            case MATURED:
                o = new Obligation(this.holder, this.issuer, new GBP(faceValue));
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
                this.currentState = State.COUPON;
                break;
            case COUPON:
                // if all the coupons have been paid, then move the
                // contract to a matured status.
                if (this.numCoupons <= 0) {
                    this.currentState = State.MATURED;
                    // if there are more coupons to be paid, subtract the number
                    // by one.
                } else {
                    this.numCoupons--;
                }
                break;

            // if the matured payment has been made, terminate the contract.
            case MATURED:
                this.currentState = State.TERMINATED;
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
        System.out.println("FinancialInstitution " + from.getName() + " has $" + from.getInventory().getAllGoodEntries().get("GBP"));
        System.out.println("FinancialInstitution " + to.getName() + " has $" + to.getInventory().getAllGoodEntries().get("GBP"));

    }

    private enum State {
        PRINCIPAL, COUPON, DEFAULT, MATURED, TERMINATED
    }
}

