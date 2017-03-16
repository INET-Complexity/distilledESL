package demos;

import agents.*;
import contracts.*;

import java.util.HashSet;

public class BoEDemo {

    private static AssetMarket assetMarket = new AssetMarket();
    private static HashSet<Agent> allAgents = new HashSet<>();

    public static void main(String[] args) {
        initialise();
        runSchedule();

    }

    private static void runSchedule() {
        assetMarket.shockPrice(Asset.AssetType.EXTERNAL, 0.05);

        for (int steps=0 ; steps<5; ++steps) {
            for (Agent agent : allAgents) {
                agent.act();
            }
            assetMarket.clearTheMarket();
        }
    }


    private static void initialise() {
        Bank bank1 = new Bank("Bank 1");
        Bank bank2 = new Bank("Bank 2");
        Hedgefund hf1 = new Hedgefund("Hedgefund 1");
        AssetManager am1 = new AssetManager("AssetManager 1");
        Investor inv1 = new Investor("Investor 1");
        CashProvider cp1 = new CashProvider("Cash Provider 1");


        initAgent(bank1, 20, 20, 20, 20, 20,
                20, 20, 20);

        initAgent(bank2, 15, 15, 13, 30, 30,
                30, 30, 30);

        initAgent(hf1, 20, 20, 20, 20, 0,
                0, 0, 0);

        initAgent(am1, 20, 20, 20, 20, 0,
                0, 0, 0);

        initAgent(inv1, 0, 0, 0, 0, 0,
                0, 0, 0);

        initAgent(cp1, 0, 0, 0, 0, 0,
                0, 0, 0);

        initRepo(bank1, hf1, 20.0);
        initRepo(bank2, hf1, 20.0);
        initRepo(cp1, bank1, 20.0);
        initRepo(cp1, bank2, 20.0);
        initInterBankLoan(bank1, bank2, 30.0);
        initShares(inv1, am1, 200);


    }

    private static void initAgent(Agent agent, double cash, double mbs, double equities, double bonds,
                                  double otherAsset, double deposits, double longTerm, double otherLiability) {

        agent.addCash(cash);
        // Asset side
        if (mbs > 0) agent.add(new AssetCollateral(agent, Asset.AssetType.MBS, assetMarket, mbs));
        if (equities >0) agent.add(new AssetCollateral(agent, Asset.AssetType.EQUITIES, assetMarket, equities));
        if (bonds > 0) agent.add(new AssetCollateral(agent, Asset.AssetType.CORPORATE_BONDS, assetMarket, bonds));
        if (otherAsset > 0) agent.add(new Other(agent, null, otherAsset));

        // Liability side
        if (deposits > 0) agent.add(new Deposit(null, agent, deposits));
        if (longTerm > 0) agent.add(new LongTermUnsecured(agent, longTerm));
        if (otherLiability > 0) agent.add(new Other(null, agent, otherLiability));


        allAgents.add(agent);
    }

    /**
     * If the INTERBANK_CONTAGION is switched on, a standard loan is created. If it is switched off however,
     * two loans are created, each of them pointing to one bank on one side, and to 'null' on the other. Remember
     * that the 'null' agent represents an agent that does nothing but has infinite liquidity and pays immediately.
     *
     * @param lender asset party
     * @param borrower liability party
     * @param principal principal
     */
    private static void initInterBankLoan(Agent lender, Agent borrower, double principal) {
        if (Parameters.INTERBANK_CONTAGION) {
            Loan loan = new Loan(lender, borrower, principal);
            lender.add(loan);
            borrower.add(loan);
        } else {
            Loan loan1 = new Loan(lender, null, principal);
            Loan loan2 = new Loan(null, borrower, principal);
            lender.add(loan1);
            borrower.add(loan2);
        }
    }

    /**
     * Similar to initInterBankLoan. If FUNDING_CONTAGION is switched on, the repo is as expected. If it is switched
     * off, two copies of the repo are made: each one points at one agent on one side, and at the 'null' agent on
     * the other side. The 'null' agent pledges all necessary collateral immediately, never defautls, and pays immediately.
     *
     * @param lender asset party (reverse-repo party)
     * @param borrower liability party (repo party)
     * @param principal principal
     */
    private static void initRepo(Agent lender, Agent borrower, double principal) {
        if (Parameters.FUNDING_CONTAGION) {
            Repo repo = new Repo(lender, borrower, principal);
            lender.add(repo);
            borrower.add(repo);
        } else {
            Repo repo1 = new Repo(lender, null, principal);
            Repo repo2 = new Repo(null, borrower, principal);
            lender.add(repo1);
            borrower.add(repo2);
        }
    }

    private static void initShares(Agent owner, CanIssueShares issuer, int number) {
        Shares shares = new Shares(owner, issuer, number);
        owner.add(shares);
        ((Agent) issuer).add(shares);
    }

}