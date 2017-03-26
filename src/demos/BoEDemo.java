package demos;

import agents.*;
import behaviours.DefaultException;
import contracts.*;

import java.util.HashSet;
import java.util.Locale;

public class BoEDemo {

    private static AssetMarket assetMarket = new AssetMarket();
    private static HashSet<Agent> allAgents = new HashSet<>();
    private static Recorder recorder;
    public static int timeStep = 0;

    public static int getTime() { return timeStep; }

    public static void main(String[] args) {
        Locale.setDefault(Locale.UK);
        initialise();
        runSchedule();
        finish();

    }

    private static void runSchedule() {

        initialShock(Parameters.ASSET_TO_SHOCK, Parameters.INITIAL_SHOCK);

        while (timeStep< Parameters.SIMULATION_TIMESTEPS) {
            timeStep++;
            System.out.println("\nTime step: "+timeStep+"\n^^^^^^^^^^^^^");

            for (Agent agent : allAgents) {
                agent.act();
            }

            assetMarket.clearTheMarket();
            recorder.record();
        }
    }

    private static void finish() {
        recorder.finish();
    }

    private static void initialise() {
        Bank bank1 = new Bank("Bank 1");
        Bank bank2 = new Bank("Bank 2");
        Bank bank3 = new Bank("Bank 3");
        Hedgefund hf1 = new Hedgefund("Hedgefund 1");
        AssetManager am1 = new AssetManager("AssetManager 1");
        Investor inv1 = new Investor("Investor 1");
        CashProvider cp1 = new CashProvider("Cash Provider 1");
        //Todo: depositors should be an agent


        initAgent(bank1, 53, 0, 260, 0, 0,
                100, 150, 0); //todo: this should have 0 deposits

        initAgent(bank2, 53, 130, 0, 130, 0,
                145, 200, 0);

        initAgent(bank3, 70, 60, 100, 100, 0,
                160, 150, 0);

        initAgent(hf1, 35, 107, 107, 107, 0,
                0, 0, 0);

        initAgent(am1, 20, 130, 130, 130, 0,
                0, 0, 0);

        initAgent(inv1, 0, 0, 0, 0, 0,
                0, 0, 0);

        initAgent(cp1, 0, 0, 0, 0, 0,
                0, 0, 0);

        addExternalAsset(bank1, Asset.AssetType.EXTERNAL1, 0);
        addExternalAsset(bank2, Asset.AssetType.EXTERNAL2, 100);
        addExternalAsset(bank3, Asset.AssetType.EXTERNAL3, 200);

        initInterBankLoan(bank1, bank2, 0);
        initInterBankLoan(bank1, bank3, 40);

        initInterBankLoan(bank2, bank1, 0);
        initInterBankLoan(bank2, bank3, 40);

        initInterBankLoan(bank3, bank1, 40);
        initInterBankLoan(bank3, bank2, 40);


        initShares(inv1, am1, 410);

        initRepo(bank1, hf1, 150.0);
        initRepo(bank2, hf1, 150.0);
        initRepo(bank3, hf1, 0.0);

        initRepo(cp1, bank1, 200);
        initRepo(cp1, bank2, 200);
        initRepo(cp1, bank3, 200);

        recorder = new Recorder(allAgents, assetMarket);
        recorder.init();
        recorder.record();
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

    private static void addExternalAsset(Agent agent, Asset.AssetType assetType, double quantity) {
        if (quantity > 0) agent.add(new Asset(agent, assetType, assetMarket, quantity));
    }

    /**
     * If the FUNDING_CONTAGION_INTERBANK is switched on, a standard loan is created. If it is switched off however,
     * two loans are created, each of them pointing to one bank on one side, and to 'null' on the other. Remember
     * that the 'null' agent represents an agent that does nothing but has infinite liquidity and pays immediately.
     *
     * @param lender asset party
     * @param borrower liability party
     * @param principal principal
     */
    private static void initInterBankLoan(Agent lender, Agent borrower, double principal) {
        if (Parameters.FUNDING_CONTAGION_INTERBANK) {
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
     * Similar to initInterBankLoan. If FUNDING_CONTAGION_HEDGEFUND is switched on, the repo is as expected. If it is switched
     * off, two copies of the repo are made: each one points at one agent on one side, and at the 'null' agent on
     * the other side. The 'null' agent pledges all necessary collateral immediately, never defautls, and pays immediately.
     *
     * @param lender asset party (reverse-repo party)
     * @param borrower liability party (repo party)
     * @param principal principal
     */
    private static void initRepo(Agent lender, Agent borrower, double principal) {
        if (principal > 0) {
            if (Parameters.FUNDING_CONTAGION_HEDGEFUND) {
                Repo repo = new Repo(lender, borrower, principal);
                lender.add(repo);
                borrower.add(repo);
                try {
                    repo.marginCall();
                } catch (FailedMarginCallException e) {
                    System.out.println("Strange! A Margin call failed at initialisation.");
                    System.exit(-1);
                }

            } else {
                Repo repo1 = new Repo(lender, null, principal);
                Repo repo2 = new Repo(null, borrower, principal);
                lender.add(repo1);
                borrower.add(repo2);
            }
        }
    }


    private static void initShares(Agent owner, CanIssueShares issuer, int number) {
        Shares shares = issuer.issueShares(owner, number);
        owner.add(shares);
        ((Agent) issuer).add(shares);
    }

    private static void initialShock(Asset.AssetType assetType, double fraction) {
        for (Agent agent : allAgents) {
            agent.receiveShockToAsset(assetType, fraction);
        }

        assetMarket.setPrice(assetType, assetMarket.getPrice(assetType) * (1.0 - fraction));
    }

    public static void devalueCommonAsset(Asset.AssetType assetType, double priceLost) {

        allAgents.forEach(agent ->
        agent.devalueAssetOfType(assetType, priceLost));
    }

}