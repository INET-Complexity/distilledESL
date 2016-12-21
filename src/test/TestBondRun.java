package test;

import ESL.contract.handler.AutomaticContractHandler;
import ESL.inventory.Contract;
import components.agents.Bank;
import components.items.Bond;
import components.items.GBP;
import sim.engine.SimState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TestBondRun extends SimState {
    public int NUMBER_OF_GOVERNMENTS=2;
    public int NUMBER_OF_BUYERS=2;


    public TestBondRun(long seed)
    {
        super(seed);
    }

    public void start(){

        super.start(); // reuse the SimState start method

        initialisePrices();
        initialiseValuationFunctions();

        List<Bank> governments = new ArrayList<>();
        List<Bank> buyers = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_GOVERNMENTS; i++) {
            Bank government = new Bank("Government " + i);
            government.add(new GBP(1000000.0));
          //  government.add(new Stock(100.0));
            governments.add(government);

        }

        for (int i = 0; i < NUMBER_OF_BUYERS; i++) {
            Bank buyer = new Bank("Buyer " + i);
            buyer.add(new GBP(10000.0));
            buyers.add(buyer);
        }

        purchaseBonds(governments, buyers);

    }


    private void purchaseBonds(List<Bank> governments, List<Bank> buyers) {

        AutomaticContractHandler handler = new AutomaticContractHandler();

        for (Bank government : governments) {
            for (Bank buyer : buyers) {
                Bond bondContract = new Bond("bond", this, handler,
                        government, buyer, 1000.0, 1000.0, 0.05,
                        24, 1.0);

                government.add(bondContract);
                buyer.add(bondContract);

                bondContract.start(this);
            }
            System.out.println("Valuation is "+government.getInventory().asset_value(prices, government));
        }

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

    public static void main(String[] args)
    {
        doLoop(TestBondRun.class, args);
        System.exit(0);
    }

}
