package test;

import ESL.contract.handler.AutomaticContractHandler;
import components.FinancialInstitution;
import components.items.*;
import components.markets.StockMarket;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

import java.util.ArrayList;
import java.util.List;

public class TestHedgefunds extends SimState implements Steppable {

    private int NUMBER_OF_HEDGEFUNDS=1;
    private ArrayList<FinancialInstitution> hedgefunds;
    private int NUMBER_OF_CASHPROVIDERS=1;
    private ArrayList<FinancialInstitution> cashproviders;
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
            FinancialInstitution newHedgeFund = new FinancialInstitution("Hedgefund "+i);
            newHedgeFund.setStockMarket(stockMarket);
            initialiseInventory(newHedgeFund);

            hedgefunds.add(newHedgeFund);
        }

        for (int i = 0; i < NUMBER_OF_CASHPROVIDERS; i++) {
            FinancialInstitution cashprovider = new FinancialInstitution("Cash Provider " + i);
            cashprovider.add(new GBP(1000000.0));
            cashproviders.add(cashprovider);

        }

        //getFunding(cashproviders, hedgefunds);

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

        for (FinancialInstitution hedgefund : hedgefunds) {
            hedgefund.step();
            //bank.getBehaviour().checkLeverageAndAct();
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
        for (FinancialInstitution hedgefund : hedgefunds) {
            everyoneAlive = everyoneAlive && hedgefund.alive;
        }

        return everyoneAlive;
    }


    private void initialiseInventory(FinancialInstitution agent) {
        agent.add(new GBP(100.0));
        agent.add(new Stock(160.0));
        agent.add(new SampleLiability(agent.getInventory().asset_value(stockMarket.prices, null)*
                (1.0-agent.getBehaviour().LEVERAGE_TARGET)));
        agent.printBalanceSheet();
    }

    private void initialCreditShock() {

        System.out.println("Attention! A shock has arrived!");
        stockMarket.setPrice(0.95);
    }

    private void getFunding(List<FinancialInstitution> cashproviders, List<FinancialInstitution> hedgefunds) {

        AutomaticContractHandler handler = new AutomaticContractHandler();

        for (FinancialInstitution cashprovider : cashproviders) {
            for (FinancialInstitution hedgefund : hedgefunds) {
                Bond funding = new Bond("funding", this, handler,
                        cashprovider, hedgefund, 1000.0, 1000.0, 0.05, 0,0.0);

                cashprovider.add(funding);
                hedgefund.add(funding);

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
