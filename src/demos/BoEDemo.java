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

        initRepo(bank1, hf1, 20.0);
        initRepo(bank2, hf1, 20.0);
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

    private static void initInterBankLoan(Agent lender, Agent borrower, double principal) {
        Loan loan = new Loan(lender, borrower, principal);
        lender.add(loan);
        borrower.add(loan);
    }

    private static void initRepo(Agent lender, Agent borrower, double principal) {
        Loan loan = new Repo(lender, borrower, principal);
        lender.add(loan);
        borrower.add(loan);
    }

    private static void initShares(Agent owner, CanIssueShares issuer, int number) {
        Shares shares = new Shares(owner, issuer, number);
        owner.add(shares);
        ((Agent) issuer).add(shares);
    }

}