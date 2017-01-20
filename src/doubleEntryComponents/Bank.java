package doubleEntryComponents;

import doubleEntry.Account;
import doubleEntry.Ledger;

public class Bank extends Agent {
    public Bank(String name) {
        this.name=name;
        generalLedger = new Ledger();
    }

    private Ledger generalLedger;
    private String name;

    public void addAccount(Account account) {
        generalLedger.addAccount(account);
    }
}
