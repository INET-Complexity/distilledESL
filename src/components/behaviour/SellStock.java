package components.behaviour;


import ESL.agent.Agent;
import components.agents.FinancialInstitution;
import components.items.Stock;
import components.markets.StockMarket;

public class SellStock implements Action {
    public SellStock(double max, FinancialInstitution agent, StockMarket market) {
        this.max=max;
        this.agent = agent;
        this.market = market;
    }

    public void perform(double amount) {
        if (amount > max) {
            System.out.println("Error. Trying to sell more Stock than there is in the inventory");
        } else {
            agent.remove(new Stock(amount));
            market.putForSale(amount);
        }
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    private double max;
    private FinancialInstitution agent;
    private StockMarket market;

}
