package components;

import ESL.inventory.Contract;

import java.util.Map;
import java.util.function.BiFunction;

public class GBP extends Cash {

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
