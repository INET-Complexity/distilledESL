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
 * A standard, fixed rate bond. The bond allows for an issue price that is different from its principal.
 * It includes the states:
 * - PRINCIPAL, when the issue price is transacted
 * - COUPON, when the coupon payments are made with a specified frequency,
 * - MATURED, when all the coupon payments have been made
 * - DEFAULT, if a payment obligation is rejected
 * - TERMINATED, when it is inactive and ready to be destructed
 *
 *
 *
 * @author rafa
 */
public class Bond extends MasonScheduledContracts {
    private State currentState;
    private Agent issuer;
    private Agent holder;
    private Double faceValue;
    private Double issuePrice;
    private Double rate;
    private Integer numCoupons;
    private Double couponFrequency;

    /**
     * Constructor allowing for an issue price different to the face value.
     * @param name name of the bond
     * @param state the SimState (the main simulation)
     * @param handler a contract handler that will deal with the obligations
     * @param issuer the party that issues the bond, i.e. the borrower
     * @param holder the party that holds the bond, i.e. the lender
     * @param faceValue the principal
     * @param issuePrice the payment made at the issuance of the bond, which can be different from the principal
     * @param rate the fixed rate for the coupons, with respect to the FACE VALUE (not the issue price)
     * @param numCoupons the number of coupons
     * @param couponFrequency the time in timesteps (for the moment) between subsequent coupon payments
     */
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

    /**
     * This constructor uses the same issue price and face value.
     */
    public Bond(String name, SimState state, ContractHandler handler, Agent issuer, Agent holder, Double faceValue,
                Double rate, Integer numCoupons, Double couponFrequency) {
        this(name, state, handler, issuer, holder, faceValue, faceValue, rate, numCoupons, couponFrequency);
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
                break;

            case TERMINATED:
                // if the bond is terminated and I'm handling a response, something's gone wrong
                System.out.println("Strange: I'm a terminated bond and I'm handling a response?");
                break;

        }

    }

    /**
     * We call this to roll over the coupon when it's matured. The number of coupons left is increased.
     * @param numCoupons how many more future coupon payments to extend to this bond
     */
    private void rollOver(int numCoupons) {
        this.currentState = State.COUPON;
        this.numCoupons = numCoupons;
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
        System.out.println("FinancialInstitution " + from.getName() + " has £" + from.getInventory().getAllGoodEntries().get("GBP"));
        System.out.println("FinancialInstitution " + to.getName() + " has £" + to.getInventory().getAllGoodEntries().get("GBP"));

    }

    private enum State {
        PRINCIPAL, COUPON, DEFAULT, MATURED, TERMINATED
    }
}

