package components.behaviour;

import components.agents.FinancialInstitution;
import components.items.Stock;

import java.util.ArrayList;
import java.util.Map;

public class Behaviour {

    private final double MINIMUM_LEVERAGE=5.5/100;
    private final double LEVERAGE_BUFFER=7.0/100;
    public final double LEVERAGE_TARGET=8.5/100;

    public BehaviouralChoices behaviouralChoice;



    public Behaviour(FinancialInstitution agent) {
        this.agent=agent;
        this.behaviouralChoice = BehaviouralChoices.CASH_FIRST;
    }



    // TODO Here we need a constructor where we can set our behavioural choice

    public void checkLeverageAndAct() {
        double currentLeverage = getLeverage();
        System.out.println(agent.getName()+" is checking its leverage target.");
        agent.printBalanceSheet();
        System.out.println("My current leverage is "+currentLeverage*100.0+"%");

        if (checkForDefault()) {
            triggerDefault();
        } else if (currentLeverage <= LEVERAGE_BUFFER) {
            getBackOnTarget(getSizeOfAction());
            currentLeverage = getLeverage();
            System.out.println("After action, my new leverage is: "+currentLeverage*100+"%");
        }

    }

    public void getBackOnTarget(double sizeOfAction) {
        System.out.println("I'm trying to get back on target by delevering an amount: £"+sizeOfAction);
        switch (behaviouralChoice) {

            case CASH_FIRST:

                double cash = agent.getInventory().getAllGoodEntries().get("GBP");
                System.out.println("I have £"+cash+" in cash.");

                // I have enough cash to clear the whole thing
                if (cash >= sizeOfAction ) {
                    System.out.println("I can hit the target with cash alone");
                    agent.payLiabilityWithCash(sizeOfAction);
                }

                // I have some cash but not enough
                else if (cash > 0) {
                    System.out.println("I will use cash but I will need to use the stock too");
                    agent.payLiabilityWithCash(cash);
                    getBackOnTarget(sizeOfAction-cash);
                }

                // I have no cash!
                else {
                    double stockValue = agent.getInventory().getAllGoodEntries().get("Stock")*Stock.getPrice();
                    System.out.println("I have £"+stockValue+" worth of stock.");

                    if (stockValue >= sizeOfAction) {
                        System.out.println("I can hit the target by selling stock");
                        agent.payLiabilityWithStock(1.0*sizeOfAction/Stock.getPrice());
                    } else if (stockValue > 0) {
                        System.out.println("I can sell some stock but it won't be enough");
                        agent.payLiabilityWithStock(1.0*stockValue/Stock.getPrice());
                        getBackOnTarget(sizeOfAction-1.0*stockValue/Stock.getPrice());
                    } else {
                        if(checkForDefault()) {
                            triggerDefault();
                        }
                    }

                }
                break;
        }
    }

    private double getSizeOfAction() {
        double assetValue = agent.getInventory().asset_value(agent.stockMarket.prices,null);
        double liabilityValue = -1.0*agent.getInventory().liability_value(agent.stockMarket.prices,null);
        return assetValue - 1.0*(assetValue-liabilityValue)/LEVERAGE_TARGET;
    }
    private double getLeverage() {
        double assetValue = agent.getInventory().asset_value(agent.stockMarket.prices,null);
        double liabilityValue = -1.0*agent.getInventory().liability_value(agent.stockMarket.prices,null);
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

    private FinancialInstitution agent;

    public enum BehaviouralChoices {
        CASH_FIRST, STOCK_FIRST, PROPORTIONAL
    }

}
