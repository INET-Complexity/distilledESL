package ESL.inventory;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by taghawi on 10/21/16.
 */
public class Good extends Item {
    
	private double quantity;

    public Good(String name, double quantity) {
        super(name); 
        this.quantity = quantity;
    }

    public Double valuation(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        return (Double) parameters.get("price_" + this.getName()) * quantity;
    }
    public Double valuation(Map<Object, Object> parameters, BiFunction<Contract, Map, Double> value_function) {
        return (Double) parameters.get("price_" + this.getName()) * quantity;
    }
    
    public double getQuantity() {
    	return this.quantity;
    }
    public double getValue() {return 0.0;}
}