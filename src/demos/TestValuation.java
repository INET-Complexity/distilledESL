//package test;
//
//import ESL.contract.handler.AutomaticContractHandler;
//import components.institutions.Bank;
//import components.items.Bond;
//import components.items.GBP;
//import sim.engine.SimState;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestValuation extends SimState {
//    public int NUMBER_OF_GOVERNMENTS=2;
//    public int NUMBER_OF_BUYERS=2;
//
//    public TestValuation(long seed)
//    {
//        super(seed);
//    }
//
//    public void start(){
//
//        super.start(); // reuse the SimState start method
//
//        List<Bank> governments = new ArrayList<>();
//        List<Bank> buyers = new ArrayList<>();
//
//        for (int i = 0; i < NUMBER_OF_GOVERNMENTS; i++) {
//            Bank government = new Bank("Government " + i);
//            government.add(new GBP(1000000.0));
//            governments.add(government);
//        }
//
//        for (int i = 0; i < NUMBER_OF_BUYERS; i++) {
//            Bank buyer = new Bank("Buyer " + i);
//            buyer.add(new GBP(10000.0));
//            buyers.add(buyer);
//        }
//
//        //government.getInventory().asset_value(null, null);
//
//        //purchaseBonds(governments, buyers);
//
//    }
//
//
//    private void purchaseBonds(List<Bank> governments, List<Bank> buyers) {
//
//        AutomaticContractHandler handler = new AutomaticContractHandler();
//
//        for (Bank government : governments) {
//            for (Bank buyer : buyers) {
//                Bond bondContract = new Bond("bond", this, handler,
//                        government, buyer, 1000.0, 1000.0, 0.05,
//                        24, 1.0);
//
//                government.add(bondContract);
//                buyer.add(bondContract);
//
//                bondContract.start(this);
//                //government.getInventory().asset_value(null, null);
//            }
//        }
//
//    }
//
//
//    public static void main(String[] args)
//    {
//        doLoop(TestValuation.class, args);
//        System.exit(0);
//    }
//
//}
