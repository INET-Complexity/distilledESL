package demos;

import actions.BankLeverageConstraint;
import actions.HedgefundLeverageConstraint;
import actions.LCR_Constraint;
import agents.Bank;
import agents.Hedgefund;
import contracts.*;

public class CollateralDemo {

    private static AssetMarket assetMarket = new AssetMarket();

    public static void main(String[] args) {
        Bank bank1 = new Bank("Bank 1");
        Bank bank2 = new Bank("Bank 2");
        Hedgefund hedgeFund = new Hedgefund("HedgeFund 1");

        initBank1(bank1);
        initBank2(bank2);
        initHedgefund(hedgeFund);
        initRepos(bank1, bank2, hedgeFund);

        runSchedule(bank1, bank2, hedgeFund);

    }

    private static void runSchedule(Bank bank1, Bank bank2, Hedgefund hedgefund) {
        System.out.println("Time t=0.");
        bank1.printBalanceSheet();
        bank2.printBalanceSheet();
        hedgefund.printBalanceSheet();

        System.out.println("Shock arrives!");
        assetMarket.shockPrice(Asset.AssetType.EXTERNAL, 1.0*(17-15)/17);
        updateAssetPrices(bank1, bank2, hedgefund);
        bank1.printBalanceSheet();

        bank1.act();
        assetMarket.clearTheMarket();
        bank1.printBalanceSheet();
        updateAssetPrices(bank1, bank2, hedgefund);
        System.out.println("price of A1 :"+assetMarket.getPrice(Asset.AssetType.MBS));

        hedgefund.printBalanceSheet();
        hedgefund.act();
        hedgefund.printBalanceSheet();

        bank2.act();
        bank2.printBalanceSheet();
    }

    private static void initBank1(Bank bank) {
        bank.addCash(20.0);//
        bank.add(new AssetCollateral(bank, Asset.AssetType.EXTERNAL, assetMarket, 17.0));
        bank.add(new AssetCollateral(bank, Asset.AssetType.MBS, assetMarket, 40.0));
        bank.setBankLeverageConstraint(new BankLeverageConstraint(bank, 5.0/100, 3.0/100, 1.0/100));
        bank.setLCR_constraint(new LCR_Constraint(bank, 1.0, 1.0, 1.0, 20.0));
    }

    private static void initBank2(Bank bank) {
        bank.addCash(20);
        bank.add(new AssetCollateral(bank, Asset.AssetType.EQUITIES, assetMarket, 40.0));
        bank.add(new AssetCollateral(bank, Asset.AssetType.CORPORATE_BONDS, assetMarket, 17.0));
        bank.setBankLeverageConstraint(new BankLeverageConstraint(bank, 5.0/100, 4.0/100, 3.0/100));
        bank.setLCR_constraint(new LCR_Constraint(bank, 1.0, 1.0, 1.0, 20.0));

    }

    private static void initHedgefund(Hedgefund hedgefund) {
        hedgefund.addCash(7.9167);
        hedgefund.add(new AssetCollateral(hedgefund, Asset.AssetType.MBS, assetMarket, 20.0));
        hedgefund.add(new AssetCollateral(hedgefund, Asset.AssetType.EQUITIES, assetMarket, 20.0));
        hedgefund.setBankLeverageConstraint(new HedgefundLeverageConstraint(hedgefund, 4.0/100, 3.0/100, 2.0/100));

    }

    private static void initRepos(Bank bank1, Bank bank2, Hedgefund hedgefund) {
        Repo loan1H = new Repo(bank1,hedgefund,23.0);
        bank1.add(loan1H);
        hedgefund.add(loan1H);
        loan1H.marginCall();

        Repo loan2H = new Repo(bank2, hedgefund, 23.0);
        bank2.add(loan2H);
        hedgefund.add(loan2H);
        loan2H.marginCall();


        bank1.add(new Loan(null, bank1, 95.0));
        bank2.add(new Loan(null, bank2, 95.0));
    }

    private static void updateAssetPrices(Bank bank1, Bank bank2, Hedgefund hedgefund) {
        bank1.updateAssetPrices();
        bank2.updateAssetPrices();
        hedgefund.updateAssetPrices();
    }
}
