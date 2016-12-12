package components;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import ESL.inventory.Inventory;
import components.behaviour.Action;
import components.behaviour.Behaviour;
import components.items.GBP;
import components.items.SampleLiability;
import components.items.Stock;
import components.markets.StockMarket;

import java.util.ArrayList;

public class FinancialInstitution extends Agent {
    private Behaviour behaviour;
    public StockMarket stockMarket;
    public boolean alive;

    public FinancialInstitution(String name) {
        super(name);
        this.behaviour = new Behaviour(this);
        this.alive = true;
    }

    public Behaviour getBehaviour() {
        return this.behaviour;
    }

    public void setStockMarket(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    public FinancialInstitution() {
        this("");
    }

    public void add(Contract contract) {
        try {
            getInventory().add(contract);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void add(Good good) {
        try{
            getInventory().add(good);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void payLiabilityWithCash(double amount) {
        try {
            System.out.println("I'm paying £"+amount+" of cash to pay off liabilities.");
            getInventory().remove(new GBP(amount));
            getInventory().remove(new SampleLiability(amount));
            printBalanceSheet();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void payLiabilityWithStock(double amount) {
        try {
            getInventory().remove(new Stock(amount));
            stockMarket.putForSale(amount);
            getInventory().remove(new SampleLiability(amount*stockMarket.getPrice()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void triggerDefault() {
        this.alive = false;
    }

    public void printBalanceSheet() {
        System.out.println("My name is "+getName());
        System.out.println("Total ASSET value: "+this.getInventory().asset_value(stockMarket.prices,null));
        System.out.println("Total LIABILITY value: "+this.getInventory().liability_value(stockMarket.prices, null));
        System.out.println();
    }

    public void step() {
        behaviour.checkLeverageAndAct();
    }


    public void setBehaviour(Behaviour behaviour) {
        this.behaviour=behaviour;
    }

    /**
     *
     * @return all available actions that do NOT break any constraints or regulations
     */
    public ArrayList<Action> getAvailableActions() {
        // TODO: Define my available actions

        return null;
    }
}
