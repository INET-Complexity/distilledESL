package test;

import doubleEntryComponents.Bank;
import doubleEntryComponents.actions.BankBehaviour1;
import doubleEntryComponents.actions.LeverageConstraint;
import doubleEntryComponents.contracts.Asset;
import doubleEntryComponents.contracts.AssetMarket;
import doubleEntryComponents.contracts.Loan;

public class ShortSimulationDemo {

    void init() {
    }

    public static void main(String[] args) {
        Bank bank1 = new Bank("Bank 1");
        Bank bank2 = new Bank("Bank 2");
        Bank hedgeFund = new Bank("HedgeFund 1");
        AssetMarket assetMarket = new AssetMarket();

        initBank1(bank1, assetMarket);
        initBank2(bank2, assetMarket);
        initHedgefund(hedgeFund, assetMarket);
        initLoans(bank1, bank2, hedgeFund);

        initBehaviours(bank1, bank2, hedgeFund);

        bank1.printBalanceSheet();

        shockExternalAsset((1.0*(17-15)/17), assetMarket);
        bank1.updateAssetPrices();

        System.out.println(bank1.getLeverageConstraint().getLeverage());
        bank1.act();

        System.out.println("Initial equity of bank "+bank1.getName()+": "+bank1.getGeneralLedger().getEquityValue());
        System.out.println("Initial equity of bank "+bank2.getName()+": "+bank2.getGeneralLedger().getEquityValue());
        System.out.println("Initial equity of HF: "+hedgeFund.getGeneralLedger().getEquityValue());


    }

    private static void initBehaviours(Bank bank1, Bank bank2, Bank hedgefund) {
        bank1.setBehaviour(new BankBehaviour1(bank1));
        bank2.setBehaviour(new BankBehaviour1(bank2));
        hedgefund.setBehaviour(new BankBehaviour1(hedgefund));

    }
    private static void initBank1(Bank bank, AssetMarket assetMarket) {
        bank.addCash(20);
        bank.add(new Asset(bank, Asset.AssetType.E, assetMarket, 17.0));
        bank.add(new Asset(bank, Asset.AssetType.A1, assetMarket, 40.0));
        bank.setLeverageConstraint(new LeverageConstraint(bank, 5.0/100, 4.0/100, 3.0/100));
    }

    private static void initBank2(Bank bank, AssetMarket assetMarket) {
        bank.addCash(20);
        bank.add(new Asset(bank, Asset.AssetType.A2, assetMarket, 40.0));
        bank.add(new Asset(bank, Asset.AssetType.A3, assetMarket, 17.0));
        bank.setLeverageConstraint(new LeverageConstraint(bank, 5.0/100, 4.0/100, 3.0/100));

    }

    private static void initHedgefund(Bank hedgefund, AssetMarket assetMarket) {
        hedgefund.addCash(9.0);
        hedgefund.add(new Asset(hedgefund, Asset.AssetType.A1, assetMarket, 20.0));
        hedgefund.add(new Asset(hedgefund, Asset.AssetType.A2, assetMarket, 20.0));
        hedgefund.setLeverageConstraint(new LeverageConstraint(hedgefund, 4.0/100, 3.0/100, 2.0/100));

    }

    private static void initLoans(Bank bank1, Bank bank2, Bank hedgefund) {
        Loan loan1H = new Loan(bank1,hedgefund,23.0);
        bank1.add(loan1H);
        hedgefund.add(loan1H);

        Loan loan2H = new Loan(bank2, hedgefund, 23.0);
        bank2.add(loan2H);
        hedgefund.add(loan2H);

        bank1.add(new Loan(null, bank1, 95.0));
        bank2.add(new Loan(null, bank2, 95.0));
    }

    private static void shockExternalAsset(double percentage, AssetMarket assetMarket) {
        assetMarket.setPriceE(assetMarket.getPrice(Asset.AssetType.E)*(1-percentage));
    }
}


