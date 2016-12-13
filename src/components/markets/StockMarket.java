package components.markets;

import components.items.Stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StockMarket {
    private final double ELASTICITY_DEMAND = 5.0/100;
    private final double VARIANCE_EXOGENOUS_SHOCKS = 1.0/100;
    public Map<Object, Object> prices = new HashMap<>();

    public StockMarket() {
        totalSupply = 0;
        prices.put("price_Stock",Stock.getPrice());
        prices.put("price_GBP", 1.0);
        prices.put("price_SampleLiability", -1.0);
    }

    public double generateNumber() {
        Random r = new Random();
        double g = VARIANCE_EXOGENOUS_SHOCKS*r.nextGaussian();
        return g;
    }


    public void step() {
        Stock.setPrice(Stock.getPrice()*(1+generateNumber()+ELASTICITY_DEMAND* totalSupply));
        prices.put("price_Stock",Stock.getPrice());
        System.out.println("The market has stepped and gotten to this price "+Stock.getPrice()+"total supply was "+totalSupply+"random number was "+generateNumber());
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
