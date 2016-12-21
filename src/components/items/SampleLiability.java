package components.items;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Action;
import components.behaviour.HasBehaviour;
import components.behaviour.PayOffSampleLiability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SampleLiability extends Good implements HasBehaviour {

    public SampleLiability(double amount) {
        super("SampleLiability", amount);
    }

    @Override
    public Double valuation(Map<Object, Object> parameters, BiFunction<Contract, Map, Double> value_function) {
        return -1.0*getQuantity();
    }

    @Override
    public Double valuation(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        return -1.0*getQuantity();
    }

    @Override
    public List<Action> getAvailableActions(Agent agent) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new PayOffSampleLiability());
        return actions;
    }
}
