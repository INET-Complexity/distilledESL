package components.markets;

import ESL.inventory.Item;
import components.Parameters;
import components.items.Stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockMarket implements Market {
    private final double ELASTICITY_DEMAND = 0.001/100;
    private final double VARIANCE_EXOGENOUS_SHOCKS = 1.0/100;
    private Parameters globalParameters;

    public StockMarket(Parameters globalParameters) {
        this.globalParameters = globalParameters;
        price = 1.0;
        totalSupply = 0;
        globalParameters.put("price_Stock",price);
        globalParameters.put("price_GBP", 1.0);
        globalParameters.put("price_SampleLiability", -1.0);

        //Todo who should be doing these things?
    }

    public void step() {
        setPrice(price * (1- ELASTICITY_DEMAND*totalSupply));
        globalParameters.put("price_Stock",getPrice());
        totalSupply=0;
    }

    public void setPrice(double amount) {
        price = amount;
        globalParameters.put("price_Stock",price);
    }

    public void putForSale(double amount) {
        totalSupply += amount;
    }

    public void putForBuy(double amount){ totalSupply -= amount;}

    public double getPrice() {
        return price;
    }

    private double price;
    private double totalSupply;
}
