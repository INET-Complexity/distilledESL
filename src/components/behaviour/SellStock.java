package components.behaviour;


import ESL.agent.Agent;
import components.agents.FinancialInstitution;
import components.items.Stock;
import components.markets.StockMarket;

public class SellStock implements Action {

    public void perform(double amount, FinancialInstitution agent, StockMarket market) {
        if (agent.getInventory().getAllGoodEntries().get("stock") < amount) {
            System.out.println("Error. Trying to sell more Stock than there is in the inventory");
        } else {
            agent.remove(new Stock(amount));
            market.putForSale(amount);
        }
    }

}
