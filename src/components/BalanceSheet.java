package components;

import ESL.inventory.Contract;
import ESL.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BalanceSheet extends Inventory {
    private double cash;

    public BalanceSheet() {
        super();
        cash=0;

    }
    public void addCash(double amount) {
        cash+=amount;
    }

    public void withdrawCash(double amount) throws Exception {
        if (cash < amount) {
            throw new Exception("Not enough cash!");
        } else {
            cash-=amount;
        }
    }

    @Override
    public double asset_value(Map<Object, Object> parameters, HashMap<Class<?>, BiFunction<Contract, Map, Double>> value_functions) {
        return super.asset_value(parameters, value_functions) + cash;
    }

}
