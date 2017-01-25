package doubleEntry;

import doubleEntryComponents.Agent;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.contracts.Asset;
import doubleEntryComponents.contracts.Contract;
import doubleEntryComponents.contracts.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the main class implementing double entry accounting. All public operations provided by this class
 * are given as a double entry operation, i.e. a pair of (dr, cr) operations.
 *
 * A ledger contains a set of accounts, and is the interface between an agent and its accounts. Agents cannot
 * directly interact with accounts other than via a ledger.
 */
public class Ledger {

    public Ledger() {
        accounts = new HashSet<>();
        assets = new HashSet<>();
        liabilities = new HashSet<>();
        equity = new HashSet<>();
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
    private Account defaultEquityAccount;

    public double getAssetValue() {
        double assetTotal = 0;
        for (Account assetAccount : assets) {
            assetTotal+=assetAccount.getTotal();
        }
        return assetTotal;
    }


    public double getLiabilityValue() {
        double liabilityTotal = 0;
        for (Account liabilityAccount : liabilities) {
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

    private void addDefaultAssetAccount (Class<? extends doubleEntryComponents.contracts.Contract> contractSubclass, Account account) {
        //TODO handle repetitions
        defaultAssetAccounts.put(contractSubclass,account);
    }

    private void addDefaultLiabilityAccount (Class<? extends doubleEntryComponents.contracts.Contract> contractSubclass, Account account) {
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
//                if (account.getContractClass()!=null) {
//                    addDefaultAssetAccount(account.getContractClass(),account);
//                } else if (account instanceof CashAccount) {
//                    defaultCashAccount = account;
//                }
                break;

            case LIABILITY:
                liabilities.add(account);
//                if (account.getContractClass()!=null) {
//                    addDefaultLiabilityAccount(account.getContractClass(),account);
//                }
                break;

            case EQUITY:
                equity.add(account);
                break;
        }

    }

    public void addAccount(Account account, Class<? extends Contract> contractClass) {
        addAccount(account);
        if (account.getAccountType()==AccountType.ASSET) {
            addDefaultAssetAccount(contractClass,account);
        } else if (account.getAccountType()==AccountType.LIABILITY) {
            addDefaultLiabilityAccount(contractClass,account);
        }
    }

    public void addCashAccount(Account account) {
        addAccount(account);
        defaultCashAccount = account;
    }

    public void addEquityAccount(Account account) {
        addAccount(account);
        defaultEquityAccount = account;
    }

    /**
     * Adding a contract that is an asset means debiting the assets account relevant to that type of contract.
     * @param contract an asset contract to add
     */
    public void addAsset(Contract contract) {
        Account account = getAssetAccountFor(contract.getClass());

        account.debit(contract.getValue());
        defaultEquityAccount.credit(contract.getValue());

        account.addContract(contract);
    }

    /**
     * Adding a contract that is a liability means *crediting* the liabilities account relevant to that type of contract.
     * @param contract a liability contract to add
     */
    public void addLiability(Contract contract) {
        Account account = getLiabilityAccountFor(contract.getClass());

        defaultEquityAccount.debit(contract.getValue());
        account.credit(contract.getValue());

        account.addContract(contract);
    }

    public void addCash(double amount) {
        defaultCashAccount.debit(amount);
        defaultEquityAccount.credit(amount);
    }

    public void pullFunding(double amount) {
        Account loanAccount = getAssetAccountFor(Loan.class);

        defaultCashAccount.debit(amount);
        loanAccount.credit(amount);

    }

    public void payLoan(double amount) {
        Account loanAccount = getLiabilityAccountFor(Loan.class);

        loanAccount.debit(amount);
        defaultCashAccount.credit(amount);
    }

    public void sellAsset(double amount) {
        Account assetAccount = getAssetAccountFor(Asset.class);

        defaultCashAccount.debit(amount);
        assetAccount.credit(amount);
    }

    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = new ArrayList<>();
        for (Account account : accounts) {
            availableActions.addAll(account.getAvailableActions(me));
        }
        return availableActions;
    }

    public void updateAssetPrices() {
        Account assetAccount = getAssetAccountFor(Asset.class);

        for (Contract contract : assetAccount.contracts) {
            if (contract instanceof Asset) {
                Asset asset = (Asset) contract;
                if (asset.priceFell()) {
                    devalueAsset(asset.valueLost());
                    asset.updatePrice();
                }
            }
        }
    }

    private void devalueAsset(double amount) {
        Account assetAccount = getAssetAccountFor(Asset.class);

        defaultEquityAccount.debit(amount);
        assetAccount.credit(amount);
    }

    public double getCash() {
        return defaultCashAccount.getTotal();
    }

    public void printBalanceSheet() {
        System.out.println();
        System.out.println("Asset accounts:");
        System.out.println("---------------");
        for (Account account : assets) {
            System.out.println(account.getName()+" -> "+account.getTotal());
        }
        System.out.println("TOTAL ASSETS: "+getAssetValue());
        System.out.println();

        System.out.println("Liability accounts:");
        System.out.println("---------------");
        for (Account account : liabilities) {
            System.out.println(account.getName()+" -> "+account.getTotal());
        }
        System.out.println("TOTAL LIABILITIES: "+getLiabilityValue());
        System.out.println();
    }
}
