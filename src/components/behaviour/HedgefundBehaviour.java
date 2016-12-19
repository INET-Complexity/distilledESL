package components.behaviour;

import ESL.inventory.Contract;
import components.institutions.Bank;
import components.institutions.HedgeFund;
import components.items.Bond;
import components.items.Stock;
import sim.engine.SimState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class HedgefundBehaviour extends Behaviour {

    private final double MINIMUM_LEVERAGE=5.5/100;
    private final double LEVERAGE_BUFFER=7.0/100;
    public final double LEVERAGE_TARGET=8.5/100;
    private HedgeFund agent;

    public BehaviouralChoices behaviouralChoice;



    public HedgefundBehaviour(HedgeFund agent) {
        super(agent);
        this.agent=agent;
        this.behaviouralChoice = BehaviouralChoices.CASH_FIRST;
    }


    public void requestLoan(double amount, SimState simState){
      ((CashProviderBehaviour) this.agent.getCashProvider().getBehaviour()).loanRequest(amount, simState);
      }


    // TODO Here we need a constructor where we can set our behavioural choice

    // TODO we need to split this checkleverageandact up

    public void checkLeverageAndAct(SimState simState) {
        initialisePrices();
        initialiseValuationFunctions();
        double currentLeverage = getLeverage();
        double cash = agent.getInventory().getAllGoodEntries().get("GBP");
        System.out.println(agent.getName()+" is checking its leverage target.");
        System.out.println("I'm checking leverage with this stockprice"+Stock.getPrice());
        agent.printBalanceSheet();
        System.out.println("My current leverage is "+currentLeverage*100.0+"%");
        double sizeOfAction = getSizeOfAction();

        if (checkForDefault()) {
            triggerDefault();
        } else if (sizeOfAction>0) {
            deLever(sizeOfAction);
            currentLeverage = getLeverage();
            System.out.println("After action, my new leverage is :"+currentLeverage*100+"%");
        } else if (sizeOfAction<0){
            if((-sizeOfAction)>cash){
                System.out.println("I'm trying to lever up by an amount: £" +sizeOfAction+"and I have £"+cash+" cash");
                requestLoan(-(sizeOfAction+cash), simState);
            leverUp(-sizeOfAction);}
            else{
                leverUp(-sizeOfAction);
                }
        }
    }


    public void leverUp(double sizeOfAction){
        System.out.println("I'm trying to lever up by an amount: £"+sizeOfAction);
        double stockValue = Stock.getPrice();
        double numberToBuy = -1.0*sizeOfAction/stockValue;
        double cash = agent.getInventory().getAllGoodEntries().get("GBP");
        if (sizeOfAction<=cash){
            System.out.println("I have "+cash+" cash");
            System.out.println("I should be removing "+1.0*sizeOfAction/stockValue+" of cash");
            agent.buyStockWithCash(1.0*sizeOfAction/stockValue);
        } else if (cash>0){
            System.out.println("I have"+cash+"cash");
            agent.buyStockWithCash(1.0*cash/stockValue);
        }
        }


    public void deLever(double sizeOfAction) {
        System.out.println("I'm trying to get back on target by delevering an amount: £"+sizeOfAction);
        switch (behaviouralChoice) {

            case CASH_FIRST:

                double cash = agent.getInventory().getAllGoodEntries().get("GBP");
                System.out.println("I have £"+cash+" in cash.");

                // I have enough cash to clear the whole thing
                if (cash >= sizeOfAction ) {
                    agent.payLiabilityWithCash(sizeOfAction);
                }

                // I have some cash but not enough
                else if (cash > 0) {
                    agent.payLiabilityWithCash(cash);
                    deLever(sizeOfAction-cash);
                }

                // I have no cash!
                else {
                    double stockValue = agent.getInventory().getAllGoodEntries().get("Stock")*Stock.getPrice();

                    if (stockValue >= sizeOfAction) {
                        agent.payLiabilityWithStock(1.0*sizeOfAction/Stock.getPrice());
                    } else if (stockValue > 0) {
                        agent.payLiabilityWithStock(1.0*stockValue/Stock.getPrice());
                        deLever(sizeOfAction-1.0*stockValue/Stock.getPrice());
                    } else {
                        if(checkForDefault()) {
                            triggerDefault();
                        };
                    }

                }
                break;
        }
    }

    private double getSizeOfAction() {
        double assetValue = agent.getInventory().asset_value(agent.stockMarket.prices,agent);
        double liabilityValue = -1.0*agent.getInventory().liability_value(agent.stockMarket.prices,agent);
        return assetValue - 1.0*(assetValue-liabilityValue)/LEVERAGE_TARGET;
    }
    private double getLeverage() {
        double assetValue = agent.getInventory().asset_value(agent.stockMarket.prices,agent);
        double liabilityValue = -1.0*agent.getInventory().liability_value(agent.stockMarket.prices,agent);
        return  (assetValue-liabilityValue)/assetValue;
    }

    private boolean checkForDefault() {
        return (getLeverage() < MINIMUM_LEVERAGE);
    }

    private void triggerDefault() {
            System.out.println(agent.getName()+" has defaulted!!");
            System.out.println("My leverage ratio is "+getLeverage());
            System.out.println("I'm out. Deal with it! Bye bye.");

            agent.triggerDefault();
    }



    Action chooseAction(ArrayList<Action> availableActions) {


        //TODO: Choose an action!
        return availableActions.get(0);
    }


    public enum BehaviouralChoices {
        CASH_FIRST, STOCK_FIRST, PROPORTIONAL
    }


    private void initialisePrices() {
        prices.put("price_GBP", 1.0);

    }

    private void initialiseValuationFunctions() {
        BiFunction<Contract, Map, Double> bondValuation = new BiFunction<Contract, Map, Double>() {
            @Override
            public Double apply(Contract contract, Map map) {

                return ((Bond) contract).getFaceValue();
            }
        };


        standardValuationFunctions.put(Bond.class,bondValuation);
    }

    HashMap<Object,Object> prices = new HashMap<>();
    HashMap<Class<?>, BiFunction<Contract, Map, Double>> standardValuationFunctions = new HashMap<>();

}
