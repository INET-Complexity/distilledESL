package components.items;

import ESL.agent.Agent;
import ESL.contract.MasonScheduledContracts;
import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.contract.obligation.ScheduledObligation;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import ESL.inventory.Item;
import components.behaviour.Action;
import sim.engine.SimState;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This class represents a standard Repo contract.
 *
 * @author rafa
 */
public class Repo extends MasonScheduledContracts {

    private State currentState;
    private Agent reverseRepoParty;
    private Agent repoParty;
    private Double rate;
    private Double loanSize;
    private Double maturity;
    private Boolean matchedBook;

    private List<Item> collateral;

    /**
     * This is the standard constructor, for a repo that is *NOT* matched book.
     *
     * @param name the name of the repo contract
     * @param state the SimState, i.e. the current simulation for Mason
     * @param handler the Contract Handler in charge of sending out the obligations
     * @param reverseRepoParty the party that holds the reverse repo in its assets, i.e. the lender
     * @param repoParty the party that took the repo, i.e. the borrower
     * @param rate the rate that must be payed on top of the loanSize used when the repo matures
     * @param loanSize the principal
     * @param maturity the time between the issuance of the repo and its repayment
     */
    public Repo(String name, SimState state, ContractHandler handler, Agent reverseRepoParty, Agent repoParty,
                Double rate, Double loanSize, Double maturity) {

        super(name, state, handler);

        currentState = State.PRINCIPAL;
        this.reverseRepoParty=reverseRepoParty;
        this.repoParty=repoParty;
        this.loanSize=loanSize;
        this.rate=rate;
        this.maturity=maturity;
        this.matchedBook=false;
    }

    /**
     * This is the constructor for a repo that is matched book with an existing repo.
     *
     * @param matchedRepo the original repo that this repo is matched with
     */
    public Repo(String name, SimState state, ContractHandler handler, Agent reverseRepoParty, Agent repoParty,
                Double rate, Double loanSize, Double maturity, Repo matchedRepo) {

        super(name, state, handler);

        currentState = State.PRINCIPAL;
        this.reverseRepoParty=reverseRepoParty;
        this.repoParty=repoParty;
        this.loanSize=loanSize;
        this.rate=rate;
        this.maturity=maturity;
        this.matchedBook=true;
        //TODO what shall we do with matched book repos?? Let's leave them out for the moment?
    }

    /**
     *
     * @param item add this balancesheet item (good or contract) to the collateral for the repo
     */
    public void addCollateral(Collateral item) {
        if (item.isEncumbered()) {
            System.out.println("Error: this item is already encumbered.");
        } else {
            this.collateral.add((Item) item);
            item.setEncumbered();
        }
    }

    public void removeCollateral(Collateral item) throws Exception {
        if (!this.collateral.contains((Item) item)) {
            throw new Exception("I'm trying to remove an item that is not currently collateral of this repo.");
        } else {
            this.collateral.remove((Item) item);
            item.setUnencumbered();
        }
    }


    /**
     * @return the value of the collateral of this repo, as a double
     */
    public double valueCollateral(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        Double value = 0.0;
        for (Item item : this.collateral) {
            if (item instanceof Contract) {
                value += value_functions.get(item.getClass()).apply((Contract)item, parameters);
            } else if (item instanceof Good) {
                value += ((Good) item).valuation(parameters, value_functions);
            }
        }
        return value;
    }


    @Override
    public List<Action> getAvailableActions(Agent agent) {
        return null;
    }

    @Override
    public ScheduledObligation requestNextObligation(SimState state) {
        return null;
    }

    @Override
    public void handleResponse(ObligationResponse response) {
 // blah blah blah
        // this is a change!
    }

    private void printObligation(Obligation o) {

    }

    private enum State {
        PRINCIPAL, DEFAULT, ROLL_OVER, MATURED, TERMINATED
    }
}
