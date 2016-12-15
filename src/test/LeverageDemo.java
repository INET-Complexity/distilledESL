package test;

import ESL.contract.handler.AutomaticContractHandler;
import components.FinancialInstitution;
import components.items.Bond;
import components.items.GBP;
import components.items.SampleLiability;
import components.items.Stock;
import components.markets.StockMarket;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

import java.util.ArrayList;
import java.util.List;

public class LeverageDemo extends SimState implements Steppable {

    private int NUMBER_OF_BANKS=1;
    private ArrayList<FinancialInstitution> banks;
    private StockMarket stockMarket;
    private int NSTEPS = 10;
    private int nstep;

    public LeverageDemo(long seed)
    {
        super(seed);
    }

    public void start(){

        super.start(); // reuse the SimState start method

        stockMarket = new
                StockMarket();


        banks = new ArrayList<>();

        for (int i=0; i < NUMBER_OF_BANKS; i++) {
            FinancialInstitution newBank = new FinancialInstitution("Bank "+i);
            newBank.setStockMarket(stockMarket);
            initialiseInventory(newBank);

            banks.add(newBank);
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

        for (FinancialInstitution bank : banks) {
            bank.step();
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
        for (FinancialInstitution bank : banks) {
            everyoneAlive = everyoneAlive && bank.alive;
        }

        return everyoneAlive;
    }


    private void initialiseInventory(FinancialInstitution agent) {
        agent.add(new GBP(0));
        agent.add(new Stock(100.0));
        agent.add(new SampleLiability(agent.getInventory().asset_value(stockMarket.prices, null)*
                (1.0-agent.getBehaviour().LEVERAGE_TARGET)));
        agent.printBalanceSheet();
    }

    private void initialCreditShock() {

        System.out.println("Attention! A shock has arrived!");
        Stock.setPrice(0.98);
    }

    private void purchaseBonds(List<FinancialInstitution> governments, List<FinancialInstitution> buyers) {

        AutomaticContractHandler handler = new AutomaticContractHandler();

        for (FinancialInstitution government : governments) {
            for (FinancialInstitution buyer : buyers) {
                Bond bondContract = new Bond("bond", this, handler,
                        government, buyer, 1000.0, 1000.0, 0.05,
                        24, 1.0);

                government.add(bondContract);
                buyer.add(bondContract);

                bondContract.start(this);
            }
        }

    }


    public static void main(String[] args)
    {
        doLoop(LeverageDemo.class, args);
        System.exit(0);
    }



    public Stoppable scheduleRepeat;
}
