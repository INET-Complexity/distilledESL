package doubleEntry;

import java.util.HashSet;

public class Ledger {

    public Ledger() {
        accounts = new HashSet<>();
        assets = new HashSet<>();
        liabilities = new HashSet<>();
        equity = new HashSet<>();
    }


    private HashSet<Account> accounts;
    private HashSet<Account> assets;
    private HashSet<Account> liabilities;
    private HashSet<Account> equity;


    public double getAssetValue() {
        double assetTotal = 0;
        for (Account assetAccount : assets) {
            assetTotal+=assetAccount.getTotal();
        }
        return assetTotal;
    }


    public double getLiabilityValue() {
        double liabilityTotal = 0;
        for (Account liabilityAccount : assets) {
            liabilityTotal+=liabilityAccount.getTotal();
        }
        return liabilityTotal;
    }

    public double getEquityValue() {
        double equityTotal = 0;
        for (Account equityAccount : equity) {
            equityTotal += equityAccount.getTotal();
        }
        return equityTotal;
    }


    public void addAccount(Account account) {
        accounts.add(account);
        switch(account.getAccountType()) {
            case ASSET:
                assets.add(account);
                break;
            case LIABILITY:
                liabilities.add(account);
                break;
            case EQUITY:
                equity.add(account);
                break;
        }
    }
}
