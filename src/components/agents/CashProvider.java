package components.agents;

import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Action;
import components.behaviour.BankBehaviour;
import components.behaviour.CashProviderBehaviour;
import components.items.GBP;
import components.items.SampleLiability;
import components.items.Stock;
import components.markets.StockMarket;

import java.util.ArrayList;

public class CashProvider extends FinancialInstitution {
    private CashProviderBehaviour cashProviderBehaviour;
    public StockMarket stockMarket;
    public HedgeFund hedgeFund;
    public boolean alive;

    public CashProvider(String name) {
        super(name);
        this.alive = true;
        setBehaviour(new CashProviderBehaviour(this));
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
        System.out.println("i'm getting+ "+amount+" of funding");
        getInventory().add(new GBP(amount));
        getInventory().add(new SampleLiability(amount));
    }

    public void payLiabilityWithStock(double amount) {
        try {
            System.out.println("I'm paying liabilities by using up a quantity "+amount+" of stock worth "+amount*stockMarket.getPrice());
//            System.out.println("Before, I had an amount of stock of: "+getInventory().getAllGoodEntries().get("Stock"));
            getInventory().remove(new Stock(amount));
//            System.out.println("Now, I have an amount of stock left of: "+getInventory().getAllGoodEntries().get("Stock"));
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
        System.out.println("Total ASSET value: "+this.getInventory().asset_value(stockMarket.prices,this));
        System.out.println("Total LIABILITY value: "+this.getInventory().liability_value(stockMarket.prices, this));
        System.out.println();
    }

    public void step() {
        ((BankBehaviour) getBehaviour()).checkLeverageAndAct();

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
