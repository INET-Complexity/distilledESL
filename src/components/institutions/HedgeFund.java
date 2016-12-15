package components.institutions;

import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Action;
import components.behaviour.BankBehaviour;
import components.behaviour.Behaviour;
import components.behaviour.HedgefundBehaviour;
import components.items.GBP;
import components.items.SampleLiability;
import components.items.Stock;
import components.markets.Market;
import components.markets.StockMarket;

import java.util.ArrayList;

public class HedgeFund extends FinancialInstitution {
    //private HedgefundBehaviour hedgefundBehaviour;
    public StockMarket stockMarket;
    public boolean alive;

    public HedgeFund(String name) {
        super(name);
        this.alive = true;
        setBehaviour(new HedgefundBehaviour(this));
    }

    public Market getMarket() {
        return this.stockMarket;
    }


    public void setStockMarket(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    public StockMarket getStockMarket(){return this.stockMarket;}

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

    public void getFreeFunding(double amount){
        System.out.println("i'm getting "+amount+" of funding");
        getInventory().add(new GBP(amount));
        getInventory().add(new SampleLiability(amount));
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

    public void buyStockWithCash(double amount){
        try{
            getInventory().add(new Stock(amount));
            stockMarket.putForBuy(amount);
            getInventory().remove(new GBP(amount*stockMarket.getPrice()));
        } catch (Exception e){
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
        ((HedgefundBehaviour) getBehaviour()).checkLeverageAndAct();

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
