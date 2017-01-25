package components.agents;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Action;
import components.behaviour.BankBehaviour;
import components.behaviour.HasBehaviour;
import components.constraints.HasLCRConstraint;
import components.constraints.HasLeverageConstraint;
import components.constraints.HasNSFRConstraint;
import components.constraints.HasRWLeverageConstraint;
import components.items.GBP;
import components.items.SampleLiability;
import components.items.Stock;
import components.markets.StockMarket;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Bank extends FinancialInstitution implements HasLeverageConstraint, HasRWLeverageConstraint,
        HasLCRConstraint, HasNSFRConstraint, HasBehaviour {

    public Bank(String name) {
        super(name);
        this.alive = true;
        setGlobalParameters(null);
        setBehaviour(new BankBehaviour(this));
    }

    public void payLiabilityWithCash(double amount) {
        try {
            System.out.println("I'm paying £"+amount+" of cash to pay off liabilities.");
            getInventory().remove(new GBP(amount));
            getInventory().remove(new SampleLiability(amount));

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

    /**
     *
     * @return all available actions that do NOT break any constraints or regulations
     */
    public ArrayList<Action> getAvailableActions(Agent agent) {
        ArrayList<Action> actions = new ArrayList<>();
        // Actions coming from each of my contracts


        // TODO: Define my available actions

        return null;
    }



    public void setStockMarket(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }
    public StockMarket getStockMarket(){return this.stockMarket;}
    public boolean isAlive() {return this.alive;}

    private boolean alive;
    private StockMarket stockMarket;

    @Override
    public double getCashAndGovtBonds() {
        return 0;
    }

    @Override
    public double getNetCashOutflows() {
        return 0;
    }

    @Override
    public double getRWAssetValue() {
        return 0;
    }

    @Override
    public double getRWLiabilityValue() {
        return 0;
    }

    @Override
    public double getRWLeverageTarget() {
        return 0;
    }

    @Override
    public double getRWMinimumLeverage() {
        return 0;
    }

    @Override
    public double getLeverageTarget() {
        return 0;
    }

    @Override
    public double getMinimumLeverage() {
        return 0;
    }

    @Override
    public double getAvailableStableFunding() {
        return 0;
    }

    @Override
    public double getRequiredStableFunding() {
        return 0;
    }
}
