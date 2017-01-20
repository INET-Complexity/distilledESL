package testDoubleEntry;

import doubleEntry.BondAssetAccount;
import doubleEntry.CashAccount;
import doubleEntryComponents.Agent;
import doubleEntryComponents.Bank;
import doubleEntryComponents.Simulation;

import java.util.HashSet;

public class InitialisationTest {

    HashSet<Agent> agents = new

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        createBanks(simulation);
    }

    void createBank(Simulation sim) {
        Bank bank1 = new Bank("Bank 1");
        initialiseBank(bank1, 2, 25, 0, 0, 0);

        Bank bank2 = new Bank("Bank 2");
        initialiseBank(bank2, 0, 0, 0, 0, 0);

        sim.addAgent(bank1);
        sim.addAgent(bank2);

    }

    void initialiseBank(Bank bank, double cash, double bonds, double loans, double stocks, double repos) {
        bank.addAccount(new CashAccount(cash));
        bank.addAccount(new BondAssetAccount(bonds));
//        bank.addAccount(new LoanAssetAccount(23.0));

//        bank.addAccount(new StocksAccount(25.0));

//        bank.addAccount(new ReverseRepoAccount(repos));
    }
}
