package components.items;

import ESL.inventory.Contract;
import ESL.inventory.Good;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Cash in GBP, dealt with as a good.
 *
 * @author rafa
 */
public class GBP extends Good implements Cash {

    public GBP(double amount) {
        super("GBP", amount);

    }

    // The valuation of GBP is just the amount
    @Override
    public Double valuation(Map<Object, Object> parameters, BiFunction<Contract, Map, Double> value_function) {
        return getQuantity();
    }

    @Override
    public Double valuation(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        return getQuantity();
    }
}
