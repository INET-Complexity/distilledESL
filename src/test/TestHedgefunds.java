//package test;
//
//import ESL.contract.handler.AutomaticContractHandler;
//import components.Parameters;
//import components.behaviour.HedgefundBehaviour;
//import components.agents.CashProvider;
//import components.agents.HedgeFund;
//import components.items.*;
//import components.markets.StockMarket;
//import sim.engine.SimState;
//import sim.engine.Steppable;
//import sim.engine.Stoppable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestHedgefunds extends SimState implements Steppable {
//
//    private int NUMBER_OF_HEDGEFUNDS=1;
//    private ArrayList<HedgeFund> hedgefunds;
//    private int NUMBER_OF_CASHPROVIDERS=1;
//    private ArrayList<CashProvider> cashproviders;
//    private StockMarket stockMarket;
//    private int NSTEPS = 10;
//    private int nstep;
//
//
//    public TestHedgefunds(long seed)
//    {
//        super(seed);
//    }
//
//    public void start(){
//
//        super.start(); // reuse the SimState start method
//
//        globalParameters = new Parameters();
//        stockMarket = new StockMarket(globalParameters);
//
//        hedgefunds = new ArrayList<>();
//        cashproviders = new ArrayList<>();
//
//
//        for (int i=0; i < NUMBER_OF_HEDGEFUNDS; i++) {
//            HedgeFund newHedgeFund = new HedgeFund("Hedgefund "+i);
//            newHedgeFund.setStockMarket(stockMarket);
//            initialiseInventory(newHedgeFund);
//            newHedgeFund.setGlobalParameters(globalParameters);
//            hedgefunds.add(newHedgeFund);
//        }
//
//        for (int i=0; i < NUMBER_OF_CASHPROVIDERS; i++) {
//            CashProvider newCashProvider = new CashProvider("CashProvider "+i);
//            newCashProvider.setStockMarket(stockMarket);
//            newCashProvider.add(new GBP(1000000));
//            newCashProvider.setGlobalParameters(globalParameters);
//            cashproviders.add(newCashProvider);
//            newCashProvider.printBalanceSheet();
//        }
//
//
//       getFunding(cashproviders, hedgefunds);
//
//        for (HedgeFund hedgefund: hedgefunds) {
//            hedgefund.add(new SampleLiability(hedgefund.getInventory().asset_value(globalParameters.getMap(), hedgefund) *
//                    (1.0 - ((HedgefundBehaviour) hedgefund.getBehaviour()).LEVERAGE_TARGET)+hedgefund.getInventory().liability_value(globalParameters.getMap(), hedgefund)));
//        }
//
//        initialCreditShock();
//
//        nstep=0;
//        scheduleRepeat = schedule.scheduleRepeating(this);
//
//
//    }
//
//    @Override
//    public void step(SimState simState) {
//
//        nstep++;
//        System.out.println();
//        System.out.println("Simulation step "+nstep);
//        System.out.println("------------------");
//
//        for (HedgeFund hedgefund : hedgefunds) {
//            hedgefund.step(simState);
//        }
//
//        stockMarket.step();
//
//        if (!isEveryoneAlive()) {
//            System.out.println("Someone defaulted. Stop the simulation");
//            simState.kill();
//        }
//
//        if (nstep>= NSTEPS) simState.kill();
//    }
//
//    private boolean isEveryoneAlive() {
//        boolean everyoneAlive = true;
//        for (HedgeFund hedgefund : hedgefunds) {
//            everyoneAlive = everyoneAlive && hedgefund.alive;
//        }
//
//        return everyoneAlive;
//    }
//
//
//    private void initialiseInventory(HedgeFund agent) {
//        agent.add(new GBP(100.0));
//        agent.add(new Stock(160.0));
//
//        agent.printBalanceSheet();
//    }
//
//    private void initialCreditShock() {
//
//        System.out.println("Attention! A shock has arrived!");
//        stockMarket.setPrice(0.95);
//    }
//
//    private void getFunding(List<CashProvider> cashproviders, List<HedgeFund> hedgefunds) {
//
//        AutomaticContractHandler handler = new AutomaticContractHandler();
//
//        for (CashProvider cashprovider : cashproviders) {
//            for (HedgeFund hedgefund : hedgefunds) {
//                Loan funding = new Loan("funding", this, handler,
//                        hedgefund, cashprovider, 1.0, 0.0, 0.0);
//
//                cashprovider.add(funding);
//                hedgefund.add(funding);
//                hedgefund.add(new GBP(funding.getPrincipal()));
//                funding.start(this);
//                hedgefund.setCashProvider(cashprovider);
//
//            }
//
//        }
//
//    }
//
//
//    public static void main(String[] args)
//    {
//        doLoop(TestHedgefunds.class, args);
//        System.exit(0);
//    }
//
//
//
//
//
//    private Parameters globalParameters;
//    public Stoppable scheduleRepeat;
//}
