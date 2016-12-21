package components.markets;

import ESL.inventory.Item;
import components.items.Stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockMarket implements Market {
    private final double ELASTICITY_DEMAND = 0.001/100;
    private final double VARIANCE_EXOGENOUS_SHOCKS = 1.0/100;
    public Map<Object, Object> prices = new HashMap<>();

    public StockMarket() {
        price = 1.0;
        totalSupply = 0;
        prices.put("price_Stock",price);
        prices.put("price_GBP", 1.0);
        prices.put("price_SampleLiability", -1.0);

        //Todo who should be doing these things?
    }


    public void setPrice(double amount) {
        price = amount;
        prices.put("price_Stock",price);
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
