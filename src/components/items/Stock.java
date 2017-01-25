package components.items;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.agents.FinancialInstitution;
import components.behaviour.Action;
import components.behaviour.HasBehaviour;
import components.behaviour.SellStock;

import java.util.ArrayList;
import java.util.List;

public class Stock extends Contract implements HasBehaviour {

    public Stock(Double amount, FinancialInstitution owner) {
        super("Stock");
        this.amount=amount;
        this.owner=owner;
        setCollateralType(new CanBeCollateral(amount));
    }

    @Override
    public List<Action> getAvailableActions(Agent agent) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new SellStock());
        return actions;
    }

    private FinancialInstitution owner;
    private double amount;

}

