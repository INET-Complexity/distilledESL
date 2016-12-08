package components;

import ESL.contract.handler.AutomaticContractHandler;
import sim.engine.SimState;

import java.util.ArrayList;
import java.util.List;

public class TestBondRun extends SimState {

    public TestBondRun(long seed)
    {
        super(seed);
    }

    public void start(){

        super.start(); // reuse the SimState start method

        List<FinancialInstitution> governments = new ArrayList<>();
        List<FinancialInstitution> buyers = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            FinancialInstitution a = new FinancialInstitution("Government " + i);
            a.getInventory().add(new GBP(1000000.0));
            governments.add(a);
        }

        for (int i = 1; i <= 2; i++) {
            FinancialInstitution a = new FinancialInstitution("Buyer " + i);
            a.getInventory().add(new GBP(10000.0));
            buyers.add(a);
        }

        this.purchaseBonds(governments, buyers);

    }
    public static void main(String[] args)
    {
        doLoop(TestBondRun.class, args);
        System.exit(0);
    }

    private void purchaseBonds(List<FinancialInstitution> governments, List<FinancialInstitution> buyers) {

        AutomaticContractHandler handler = new AutomaticContractHandler();

        for (FinancialInstitution government : governments) {
            for (FinancialInstitution buyer : buyers) {
                Bond bondContract = new Bond("bond", this, handler,
                        government, buyer, 1000.0, 30.0, 0.05,
                        24, 1.0);

                government.add(bondContract);
                buyer.add(bondContract);
                bondContract.start(this);
            }
        }

    }

}
