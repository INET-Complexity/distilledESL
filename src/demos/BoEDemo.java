package demos;

import agents.Bank;
import agents.Hedgefund;
import contracts.*;

import java.util.HashMap;

public class BoEDemo {

    private static AssetMarket assetMarket = new AssetMarket();
    private static HashMap<String, Bank> banks = new HashMap<>();
    private static HashMap<String, Hedgefund> hedgefunds = new HashMap<>();

    public static void main(String[] args) {
        initialise();
        runSchedule();

    }

    private static void runSchedule() {
        assetMarket.shockPrice(Asset.AssetType.EXTERNAL, 0.05);
//
//        for (Bank me : banks.values()) {
//            me.printBalanceSheet(); me.act();
//        }
        banks.get("Bank2").printBalanceSheet();
        banks.get("Bank2").act();
        assetMarket.clearTheMarket();
        banks.get("Bank2").printBalanceSheet();
        banks.get("Bank2").act();
        banks.get("Bank2").act();


    }


    private static void initialise() {

        initBank("Bank1", 20, 20, 20, 20, 20,
                20, 20, 20);

        initBank("Bank2", 15, 15, 13, 30, 30,
                30, 30, 30);

        initHedgefund("Hedgefund1", 20, 20, 20, 20);


        initRepo("Bank1", "Hedgefund1", 20.0);
        initRepo("Bank2", "Hedgefund1", 20.0);
        initInterBankLoan("Bank1", "Bank2", 30.0);


    }

    private static void initBank(String name, double cash, double mbs, double equities, double bonds,
                                 double otherAsset, double deposits, double longTerm, double otherLiability) {
        Bank bank = new Bank(name);
        bank.addCash(cash);

        // Asset side
        bank.add(new AssetCollateral(bank, Asset.AssetType.MBS, assetMarket, mbs));
        bank.add(new AssetCollateral(bank, Asset.AssetType.EQUITIES, assetMarket, equities));
        bank.add(new AssetCollateral(bank, Asset.AssetType.CORPORATE_BONDS, assetMarket, bonds));
        bank.add(new Other(bank, null, otherAsset));

        // Liability side
        bank.add(new Deposit(null, bank, deposits));
        bank.add(new LongTermUnsecured(bank, longTerm));
        bank.add(new Other(null, bank, otherLiability));


        banks.put(name, bank);
    }

    private static void initHedgefund(String name, double cash, double mbs, double equities, double bonds) {
        Hedgefund hf = new Hedgefund(name);
        hf.addCash(cash);

        // Asset side
        hf.add(new AssetCollateral(hf, Asset.AssetType.MBS, assetMarket, mbs));
        hf.add(new AssetCollateral(hf, Asset.AssetType.EQUITIES, assetMarket, equities));
        hf.add(new AssetCollateral(hf, Asset.AssetType.CORPORATE_BONDS, assetMarket, bonds));

        hedgefunds.put(name, hf);
    }

    private static void initInterBankLoan(String lenderName, String borrowerName, double principal) {
        Bank lender = banks.get(lenderName);
        Bank borrower = banks.get(borrowerName);
        Loan loan = new Loan(lender, borrower, principal);
        lender.add(loan);
        borrower.add(loan);
    }

    private static void initRepo(String lenderName, String borrowerName, double principal) {
        Bank lender = banks.get(lenderName);
        Hedgefund borrower = hedgefunds.get(borrowerName);
        Loan loan = new Repo(lender, borrower, principal);
        lender.add(loan);
        borrower.add(loan);
    }


}