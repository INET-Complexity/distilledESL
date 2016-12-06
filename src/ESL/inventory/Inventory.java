package ESL.inventory;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Created by taghawi on 10/21/16.
 */
public class Inventory {
    Set<Contract> contracts = new HashSet<Contract>();
    Map<String, Double> goods = new HashMap<String, Double>();
    /**
     * Adds an Item to the BalanceSheet. This method is
     * protected to maintain stock-flow consistency.
     * @param good the Item to add
     * @return returns a boolean regarding whether the add was successful.
     */
    public void add(Good good) {
        goods.put(good.getName(), goods.getOrDefault(good.getName(), 0.0) + good.getQuantity());
    }

    public void add(Contract contract) throws Exception {
        if (this.contracts.contains(contract)) {
            throw new Exception("add contract that is already present");
        } else {
            this.contracts.add(contract);
        }
    }
    public void remove(Good good) throws Exception {
        if (good.getQuantity() > this.goods.get(good.getName())) {
            throw new Exception(("not enough goods"));
        } else {
            goods.put(good.getName(), goods.get(good.getName()) - good.getQuantity());
        }
    }

    public void remove(Contract contract) {
        this.contracts.remove(contract);
    }

    public double net_value(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        Double nv = 0.0;
        for (Contract contract : this.contracts) {
            nv += value_functions.get(contract.getClass()).apply(contract, parameters);
        }
        for (Map.Entry<String, Double> entry : this.goods.entrySet()) {
            nv += entry.getValue() * (Double)parameters.get("price_" + entry.getKey());
        }
        return nv;
    }

    public double asset_value(Map<Object, Object> parameters, HashMap<Class<?>, BiFunction<Contract, Map, Double>> value_functions) {
        Double nv = 0.0;
        for (Contract contract : this.contracts) {
            double value = value_functions.get(contract.getClass()).apply(contract, parameters);
            if (value > 0) {
                nv += value;
            }
        }
        for (Map.Entry<String, Double> entry : this.goods.entrySet()) {
            double value = entry.getValue() * (Double)parameters.get("price_" + entry.getKey());
            if (value > 0) {
                nv += value;
            }
        }
        return nv;
    }

    public double liability_value(Map<Object, Object> parameters, Map<Contract, BiFunction<Contract, Map, Double>> value_functions) {
        Double nv = 0.0;
        for (Contract contract : this.contracts) {
            double value = value_functions.get(contract.getClass()).apply(contract, parameters);
            if (value < 0) {
                nv += value;
            }
        }
        for (Map.Entry<String, Double> entry : this.goods.entrySet()) {
            double value = entry.getValue() * (Double)parameters.get("price_" + entry.getKey());
            if (value < 0) {
                nv += value;
            }
        }
        return nv;
    }
    
    public HashMap<String, Double> getAllGoodEntries() {
    	HashMap<String, Double> items = new HashMap<String, Double>();
    	for (String key: this.goods.keySet()) {
    		items.put(key, this.goods.get(key));
    	}
    	
    	return items;
    }

    public HashMap<String, Double> assets(Map<Object, Object> parameters, HashMap<Class<?>, BiFunction<Contract, Map, Double>> value_functions) {
        HashMap<String, Double> items = new HashMap<String, Double>();
        for (Contract contract : this.contracts) {
            double value = value_functions.get(contract.getClass()).apply(contract, parameters);
            if (value > 0) {
                items.put(contract.getName(), value);
            }
        }
        for (Map.Entry<String, Double> entry : this.goods.entrySet()) {
            double value = entry.getValue() * (Double)parameters.get("price_" + entry.getKey());
            if (value > 0) {
                items.put(entry.getKey(), value);
            }
        }
        return items;
    }

            public HashMap<String, Double> liabilities(Map<Object, Object> parameters, HashMap<Class<?>, BiFunction<Contract, Map, Double>> value_functions) {
                HashMap<String, Double> items = new HashMap<String, Double>();
                for (Contract contract : this.contracts) {
                    double value = value_functions.get(contract.getClass()).apply(contract, parameters);
                    if (value < 0) {
                        items.put(contract.getName(), value);
                    }
                }
                for (Map.Entry<String, Double> entry : this.goods.entrySet()) {
                    double value = entry.getValue() * (Double)parameters.get("price_" + entry.getKey());
                    if (value < 0) {
                        items.put(entry.getKey(), value);
                    }
                }
                return items;
            }
}



