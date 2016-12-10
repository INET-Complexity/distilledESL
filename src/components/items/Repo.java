package components.items;

import ESL.agent.Agent;
import ESL.contract.MasonScheduledContracts;
import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.contract.obligation.ScheduledObligation;
import ESL.inventory.Item;
import sim.engine.SimState;

import java.util.List;

public class Repo extends MasonScheduledContracts {

    private State currentState;
    private Agent reverseRepoParty;
    private Agent repoParty;
    private Double rate;
    private Double loanSize;
    private Double maturity;
    private Boolean matchedBook;

    private List<Item> collateral;

    public Repo(String name, SimState state, ContractHandler handler, Agent reverseRepoParty, Agent repoParty,
                Double rate, Double loanSize, Double maturity, Boolean matchedBook) {

        super(name, state, handler);

        currentState = State.PRINCIPAL;
        this.reverseRepoParty=reverseRepoParty;
        this.repoParty=repoParty;
        this.loanSize=loanSize;
        this.rate=rate;
        this.maturity=maturity;
        this.matchedBook=matchedBook;
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
        PRINCIPAL, MARGIN_CALL, DEFAULT, ROLL_OVER, MATURED, TERMINATED
    }
}
