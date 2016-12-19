package test;

import ESL.contract.handler.AutomaticContractHandler;
import ESL.inventory.Contract;
import components.behaviour.HedgefundBehaviour;
import components.institutions.Bank;
import components.institutions.CashProvider;
import components.institutions.HedgeFund;
import components.items.*;
import components.markets.StockMarket;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TestHedgefunds extends SimState implements Steppable {

    private int NUMBER_OF_HEDGEFUNDS=1;
    private ArrayList<HedgeFund> hedgefunds;
    private int NUMBER_OF_CASHPROVIDERS=1;
    private ArrayList<CashProvider> cashproviders;
    private StockMarket stockMarket;
    private int NSTEPS = 10;
    private int nstep;


    public TestHedgefunds(long seed)
    {
        super(seed);
    }

    public void start(){

        super.start(); // reuse the SimState start method

        stockMarket = new StockMarket();

        hedgefunds = new ArrayList<>();
        cashproviders = new ArrayList<>();


        for (int i=0; i < NUMBER_OF_HEDGEFUNDS; i++) {
            HedgeFund newHedgeFund = new HedgeFund("Hedgefund "+i);
            newHedgeFund.setStockMarket(stockMarket);
            initialiseInventory(newHedgeFund);
            hedgefunds.add(newHedgeFund);
        }

        for (int i=0; i < NUMBER_OF_CASHPROVIDERS; i++) {
            CashProvider newCashProvider = new CashProvider("CashProvider "+i);
            newCashProvider.setStockMarket(stockMarket);
            newCashProvider.add(new GBP(1000000000));
            cashproviders.add(newCashProvider);
            newCashProvider.printBalanceSheet();
        }


       getFunding(cashproviders, hedgefunds);

        for (HedgeFund hedgefund: hedgefunds) {
            hedgefund.add(new SampleLiability(hedgefund.getInventory().asset_value(stockMarket.prices, hedgefund) *
                    (1.0 - ((HedgefundBehaviour) hedgefund.getBehaviour()).LEVERAGE_TARGET)+hedgefund.getInventory().liability_value(stockMarket.prices, hedgefund)));
        }

        initialCreditShock();

        nstep=0;
        scheduleRepeat = schedule.scheduleRepeating(this);


    }

    @Override
    public void step(SimState simState) {

        nstep++;
        System.out.println();
        System.out.println("Simulation step "+nstep);
        System.out.println("------------------");

        for (HedgeFund hedgefund : hedgefunds) {
            hedgefund.step();
        }

        stockMarket.step();

        if (!isEveryoneAlive()) {
            System.out.println("Someone defaulted. Stop the simulation");
            simState.kill();
        }

        if (nstep>= NSTEPS) simState.kill();
    }

    private boolean isEveryoneAlive() {
        boolean everyoneAlive = true;
        for (HedgeFund hedgefund : hedgefunds) {
            everyoneAlive = everyoneAlive && hedgefund.alive;
        }

        return everyoneAlive;
    }


    private void initialiseInventory(HedgeFund agent) {
        agent.add(new GBP(100.0));
        agent.add(new Stock(160.0));

        agent.printBalanceSheet();
    }

    private void initialCreditShock() {

        System.out.println("Attention! A shock has arrived!");
        stockMarket.setPrice(0.95);
    }

    private void getFunding(List<CashProvider> cashproviders, List<HedgeFund> hedgefunds) {

        AutomaticContractHandler handler = new AutomaticContractHandler();

        for (CashProvider cashprovider : cashproviders) {
            for (HedgeFund hedgefund : hedgefunds) {
                Loan funding = new Loan("funding", this, handler,
                        hedgefund, cashprovider, 1000.0, 0.0, 0.0);

                cashprovider.add(funding);
                hedgefund.add(funding);
                hedgefund.add(new GBP(funding.getPrincipal()));
                funding.start(this);

            }

        }

    }


    public static void main(String[] args)
    {
        doLoop(TestHedgefunds.class, args);
        System.exit(0);
    }





    public Stoppable scheduleRepeat;
}
