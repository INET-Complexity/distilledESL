package components.markets;

import components.items.Stock;

import java.util.HashMap;
import java.util.Map;

public class StockMarket {
    private final double PRICE_IMPACT = 0.05/100;
    public Map<Object, Object> prices = new HashMap<>();

    public StockMarket() {
        totalSupply = 0;
        prices.put("price_Stock",getPrice());
        prices.put("price_GBP", 1.0);
        prices.put("price_SampleLiability", -1.0);
    }

    public void step() {
        Stock.setPrice(getPrice()*(1.0 - PRICE_IMPACT * totalSupply));
        prices.put("price_Stock",getPrice());
        System.out.println("Price of stock is: "+getPrice());
        totalSupply=0;
    }

    public void putForSale(double amount) {
        totalSupply += amount;
    }

    public double getPrice() {
        return Stock.getPrice();
    }


    private double totalSupply;
}