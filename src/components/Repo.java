//package components;
//
//import ESL.agent.Agent;
//import ESL.contract.MasonScheduledContracts;
//import ESL.contract.handler.ContractHandler;
//import ESL.contract.messages.ObligationResponse;
//import ESL.contract.obligation.Obligation;
//import ESL.contract.obligation.ScheduledObligation;
//import ESL.inventory.Good;
//import ESL.inventory.Item;
//import sim.engine.SimState;
//
//import java.util.List;
//
//public class Repo extends MasonScheduledContracts {
//
//    private State currentState;
//    private Agent reverseRepoParty;
//    private Agent repoParty;
//    private Double rate;
//    private Double loanSize;
//    private Double maturity;
//    private Boolean matchedBook;
//    private List<Item> collateral;
//
//    public Repo(String name, SimState state, ContractHandler handler, Agent reverseRepoParty, Agent repoParty,
//                Double rate, Double loanSize, Double maturity, Boolean matchedBook) {
//
//        super(name, state, handler);
//
//        currentState = State.PRINCIPAL;
//        this.reverseRepoParty=reverseRepoParty;
//        this.repoParty=repoParty;
//        this.loanSize=loanSize;
//        this.rate=rate;
//        this.maturity=maturity;
//        this.matchedBook=matchedBook;
//
//        this.scheduleEvent(requestNextObligation(state), state);
//    }
//
//    @Override
//    public ScheduledObligation requestNextObligation(SimState state) {
//
//        Obligation o = null;
//        Double time = gapBetweenCoupons;
//
//        switch (this.currentState) {
//
//            case PRINCIPAL:
//                o = new Obligation(this.seller, this.buyer, new Good(this.goodName, principalPayment));
//                time = new Double(1.0);
//                break;
//            case COUPON:
//                o = new Obligation(this.buyer, this.seller, new Good(this.goodName, this.couponAmount));
//                break;
//            case MATURED:
//                o = new Obligation(this.buyer, this.seller, new Good(this.goodName, this.principalPayment));
//                break;
//        }
//
//        return (new ScheduledObligation(o, state.schedule.getSteps() + time));
//    }
//
//    @Override
//    public void handleResponse(ObligationResponse response) {
//
//        printObligation(response.getObligation());
//
//        // switch state to DEFAULT if the response is false
//        if (!response.getFilled()) {
//            this.currentState = State.DEFAULT;
//            return;
//        }
//
//        // change the state based on the response to the previous obligation
//        switch (this.currentState) {
//
//            case PRINCIPAL:
//                this.currentState = State.COUPON;
//                break;
//            case COUPON:
//                // if all the coupons have been paid, then move the
//                // contract to a matured status.
//                if (this.numCoupons <= 0) {
//                    this.currentState = State.MATURED;
//                    // if there are more coupons to be paid, subtract the number
//                    // by one.
//                } else {
//                    this.numCoupons--;
//                }
//                break;
//
//            // if the matured payment has been made, terminate the contract.
//            case MATURED:
//                this.currentState = State.TERMINATED;
//        }
//
//    }
//
//    private void printObligation(Obligation o) {
//
//        if (o == null) {
//            return;
//        }
//        Agent from = o.getFrom();
//        Agent to = o.getTo();
//        String what = o.getWhat().getName();
//        Double quantity = 1.0;
//
//        if (o.getWhat() instanceof Good) {
//            quantity = ((Good) o.getWhat()).getQuantity();
//        }
//        System.out.println("The current state is: " + this.currentState + ". Therefore, " + from.getName() + " gave "
//                + to.getName() + " " + quantity + " of " + what);
//        System.out.println("FinancialInstitution " + from.getName() + " has $" + from.getInventory().getAllGoodEntries().get("cash"));
//        System.out.println("FinancialInstitution " + to.getName() + " has $" + to.getInventory().getAllGoodEntries().get("cash"));
//
//    }
//
//    private enum State {
//        PRINCIPAL, COUPON, DEFAULT, MATURED, TERMINATED
//    }
//}
