package ESL.inventory;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by eva on 11/12/2016.
 */
public class Equity extends Good {
    public static double Price;
    private double quantity;
    private Boolean encumberedOrNot;

    public Equity(String name, double quantity, double Price, Boolean encumberedOrNot){
        super(name, quantity);
        this.Price = Price;
        this.encumberedOrNot=encumberedOrNot;
    }

    public Double valuation(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        return (Double) parameters.get("price_" + this.getName()) * quantity*Price;
    }

    public void setEncumberedOrNot(Boolean encumbered){
        this.encumberedOrNot=encumbered;
    }




}
