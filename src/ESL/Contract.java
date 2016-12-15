package ESL;

import java.util.Map;
import java.util.function.BiFunction;

public class Contract {

    private String name;

    public Contract(String name) {
        this.name = name;
    }

    public Double valutation(Map<Object, Object> parameters,
                             Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        return value_functions.get(this.getClass()).apply(this, parameters);
    }

    public Double valutation(Map<Object, Object> parameters, BiFunction<Contract, Map, Double> value_function) {
        return value_function.apply(this, parameters);
    }

}