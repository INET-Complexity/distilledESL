package ESL.inventory;

//import com.sun.org.apache.xpath.internal.functions.Function;

import ESL.agent.Agent;
import components.behaviour.Action;
import components.behaviour.HasBehaviour;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by taghawi on 10/21/16.
 */
public abstract class Contract extends Item implements HasBehaviour {

    protected Contract(String name) {
	super(name);
    }

    public Double valuation(Map<Object, Object> parameters,
	    Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
	return value_functions.get(this.getClass()).apply(this, parameters);
    }

    public Double valuation(Map<Object, Object> parameters, BiFunction<Contract, Map, Double> value_function) {
	return value_function.apply(this, parameters);
    }

    public Double default_valuation(Agent agent) {
        return null;
    }

}
