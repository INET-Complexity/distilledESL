package components.items;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Action;
import components.behaviour.HasBehaviour;
import components.behaviour.SellStock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Stock extends Good  implements CanBePledgedCollateral, HasBehaviour {
    public Stock(Double amount) {
        super("Stock",amount);
    }


    @Override
    public List<Action> getAvailableActions(Agent agent) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new SellStock());
        return actions;
    }

    @Override
    public void setEncumbered() {
        if (this.encumbered) {
            System.out.println("Strange: I'm setting this stock as encumbered but it already is.");
        }
        this.encumbered=true;
    }

    @Override
    public void setUnencumbered() {this.encumbered=false;}

    @Override
    public boolean isEncumbered() {return this.encumbered;}

    private boolean encumbered;
    private double encumberedAmount; // todo what if we only want to set some amount of equity to encumbered?


}

