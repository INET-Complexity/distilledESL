package components.items;

import ESL.inventory.Contract;
import ESL.inventory.Good;

import java.util.Map;
import java.util.function.BiFunction;

public class Stock extends Good  implements Collateral {
    public Stock(Double amount) {
        super("Stock",amount);
    }

    //TODO: PRICE OF EQUITY?
    private static double price = 1.0;

    public static void setPrice(double amount) {
        price = amount;
    }

    public static double getPrice() {
        return price;
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

    @Override
    public Double valuation(Map<Object, Object> parameters, BiFunction<Contract, Map, Double> value_function) {
        return this.getQuantity()*price;
    }

    @Override
    public Double valuation(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        return this.getQuantity()*price;
    }

    @Override
    public double getValue() {
        return this.getQuantity()*price;
    }
}

