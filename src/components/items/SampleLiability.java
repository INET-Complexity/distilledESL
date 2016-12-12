package components.items;

import ESL.inventory.Contract;
import ESL.inventory.Good;

import java.util.Map;
import java.util.function.BiFunction;

public class SampleLiability extends Good {

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
    public double getValue() {
        return -1.0*getQuantity();
    }
}
