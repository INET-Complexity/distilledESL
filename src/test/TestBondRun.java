package test;

import ESL.contract.handler.AutomaticContractHandler;
import components.items.Bond;
import components.FinancialInstitution;
import components.items.GBP;
import sim.engine.SimState;

import java.util.ArrayList;
import java.util.List;

public class TestBondRun extends SimState {
    public int NUMBER_OF_GOVERNMENTS=2;
    public int NUMBER_OF_BUYERS=2;

    public TestBondRun(long seed)
    {
        super(seed);
    }

    public void start(){

        super.start(); // reuse the SimState start method

        List<FinancialInstitution> governments = new ArrayList<>();
        List<FinancialInstitution> buyers = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_GOVERNMENTS; i++) {
            FinancialInstitution government = new FinancialInstitution("Government " + i);
            government.add(new GBP(1000000.0));
            governments.add(government);

        }

        for (int i = 0; i < NUMBER_OF_BUYERS; i++) {
            FinancialInstitution buyer = new FinancialInstitution("Buyer " + i);
            buyer.add(new GBP(10000.0));
            buyers.add(buyer);
        }

        purchaseBonds(governments, buyers);

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
        doLoop(TestBondRun.class, args);
        System.exit(0);
    }

}
