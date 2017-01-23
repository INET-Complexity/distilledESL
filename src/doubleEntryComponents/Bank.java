package doubleEntryComponents;

import components.items.Bond;
import doubleEntry.*;
import doubleEntryComponents.contracts.Contract;

public class Bank extends Agent {
    public Bank(String name) {
        this.name=name;
        generalLedger = new Ledger();

        // Add the standard accounts to the bank here

        addAccount(new CashAccount());
        //addAccount(new AssetLoansAccount());
        addAccount(new BondAssetAccount());
        addAccount(new StocksAccount());
        addAccount(new ReverseRepoAccount());
        addAccount(new OtherAssetsAccount());

    }

    @Override
    public void add(Contract contract) {
        if (contract.getAssetParty()==this) {
            // This contract is an asset for me.
            generalLedger.addAsset(contract);
        } else if (contract.getLiabilityParty()==this) {
            // This contract is a liability for me
            generalLedger.addLiability(contract);
        }
    }

    private Ledger generalLedger;
    private String name;

    public void addAccount(Account account) {
        generalLedger.addAccount(account);
    }

    public void addCash(double amount) {
        generalLedger.getDefaultCashAccount().debit(amount);
    }


}
