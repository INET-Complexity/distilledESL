package doubleEntry;

import doubleEntryComponents.contracts.Contract;

import java.util.HashMap;
import java.util.HashSet;

public class Ledger {

    public Ledger() {
        accounts = new HashSet<>();
        assets = new HashSet<>();
        liabilities = new HashSet<>();
        equity = new HashSet<>();
        defaultCashAccount = null;
        defaultAssetAccounts = new HashMap<>();
        defaultLiabilityAccounts = new HashMap<>();
    }


    private HashSet<Account> accounts;
    private HashSet<Account> assets;
    private HashSet<Account> liabilities;
    private HashSet<Account> equity;
    private HashMap<Class<? extends doubleEntryComponents.contracts.Contract>,Account> defaultAssetAccounts;
    private HashMap<Class<? extends doubleEntryComponents.contracts.Contract>,Account> defaultLiabilityAccounts;
    private Account defaultCashAccount;

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

    public void addDefaultAssetAccount (Class<? extends doubleEntryComponents.contracts.Contract> contractSubclass, Account account) {
        //TODO handle repetitions
        defaultAssetAccounts.put(contractSubclass,account);
    }

    public void addDefaultLiabilityAccount (Class<? extends doubleEntryComponents.contracts.Contract> contractSubclass, Account account) {
        //TODO handle repetitions
        defaultLiabilityAccounts.put(contractSubclass,account);
    }

    public Account getAssetAccountFor (Class<? extends Contract> contractSubclass) {
        return defaultAssetAccounts.get(contractSubclass);
    }

    public Account getLiabilityAccountFor (Class<? extends Contract> contractSubclass) {
        return defaultLiabilityAccounts.get(contractSubclass);
    }

    public Account getDefaultCashAccount() {
        return defaultCashAccount;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        switch(account.getAccountType()) {

            case ASSET:
                assets.add(account);
                if (account.getContractClass()!=null) {
                    addDefaultAssetAccount(account.getContractClass(),account);
                } else if (account instanceof CashAccount) {
                    defaultCashAccount = account;
                }
                break;

            case LIABILITY:
                liabilities.add(account);
                if (account.getContractClass()!=null) {
                    addDefaultLiabilityAccount(account.getContractClass(),account);
                }
                break;

            case EQUITY:
                equity.add(account);
                break;
        }

    }

    /**
     * Adding a contract that is an asset means debiting the assets account relevant to that type of contract.
     * @param contract an asset contract to add
     */
    public void addAsset(Contract contract) {
        Account account = getAssetAccountFor(contract.getClass());
        account.debit(contract.getValue());
        account.addContract(contract);
    }

    /**
     * Adding a contract that is a liability means *crediting* the liabilities account relevant to that type of contract.
     * @param contract a liability contract to add
     */
    public void addLiability(Contract contract) {
        Account account = getLiabilityAccountFor(contract.getClass());
        account.credit(contract.getValue());
        account.addContract(contract);
    }

}
