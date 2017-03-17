package accounting;

import agents.Agent;
import actions.Action;
import contracts.Asset;
import contracts.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the main class implementing double entry accounting. All public operations provided by this class
 * are performed as a double entry operation, i.e. a pair of (dr, cr) operations.
 *
 * A Ledger contains a set of accounts, and is the interface between an agent and its accounts. Agents cannot
 * directly interact with accounts other than via a Ledger.
 *
 * At the moment, a Ledger contains an account for each type of contract, plus an equity account and a cash account.
 *
 * A simple economic agent will usually have a single Ledger, whereas complex firms and banks can have several books
 * (as in branch banking for example).
 *
 * @author rafa
 */
public class Ledger implements LedgerAPI {
    //TODO: We need to do valuation differently!!

    public Ledger(Agent owner) {
        // A Ledger is a list of accounts (for quicker searching)
        assetAccounts = new HashSet<>();
        liabilityAccounts = new HashSet<>();
        equityAccounts = new HashSet<>();

        allAssets = new HashSet<>();
        allLiabilities = new HashSet<>();

        // Each Account includes an inventory to hold one type of contract.
        // These hashmaps are used to access the correct account for a given type of contract.
        // Note that separate hashmaps are needed for asset accounts and liability accounts: the same contract
        // type (such as Loan) can sometimes be an asset and sometimes a liability.
        contractsToAssetAccounts = new HashMap<>();
        contractsToLiabilityAccounts = new HashMap<>();

        // A book is initially created with a cash account and an equityAccounts account (it's the simplest possible book)
        cashAccount = new Account("cash", AccountType.ASSET);
        equityAccount = new Account("equityAccounts", AccountType.EQUITY);
        addAccount(cashAccount, null);
        addAccount(equityAccount, null);
    }


    private HashSet<Contract> allAssets;
    private HashSet<Contract> allLiabilities;
    private HashSet<Account> assetAccounts;
    private HashSet<Account> liabilityAccounts;
    private HashSet<Account> equityAccounts;
    private HashMap<Class<? extends contracts.Contract>, Account> contractsToAssetAccounts;
    private HashMap<Class<? extends contracts.Contract>, Account> contractsToLiabilityAccounts;
    private Account cashAccount;
    private Account equityAccount;

    public double getAssetValue() {
        double assetTotal = 0;
        for (Account assetAccount : assetAccounts) {
            assetTotal+=assetAccount.getBalance();
        }
        return assetTotal;
    }

    public double getLiabilityValue() {
        double liabilityTotal = 0;
        for (Account liabilityAccount : liabilityAccounts) {
            liabilityTotal+=liabilityAccount.getBalance();
        }
        return liabilityTotal;
    }

    public double getEquityValue() {
        double equityTotal = 0;
        for (Account equityAccount : equityAccounts) {
            equityTotal += equityAccount.getBalance();
        }
        return equityTotal;
    }

    public double getAssetValueOf(Class<?> contractType) {
//        return contractsToAssetAccounts.get(contractType).getBalance();
        return allAssets.stream()
                .filter(contractType::isInstance)
                .mapToDouble(Contract::getValue)
                .sum();
    }

    public double getLiabilityValueOf(Class<?> contractType) {
//        return contractsToLiabilityAccounts.get(contractType).getBalance();
        return allLiabilities.stream()
                .filter(contractType::isInstance)
                .mapToDouble(Contract::getValue)
                .sum();

    }

    public HashSet<Contract> getAllAssets() {
        return allAssets;
    }

    public HashSet<Contract> getAllLiabilities() {
        return allLiabilities;
    }

    public HashSet<Contract> getAssetsOfType(Class<?> contractType) {
        return allAssets.stream()
                .filter(contractType::isInstance)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public HashSet<Contract> getLiabilitiesOfType(Class<?> contractType) {
        return allLiabilities.stream()
                .filter(contractType::isInstance)
                .collect(Collectors.toCollection(HashSet::new));

    }

    public double getCash() {
        return cashAccount.getBalance();
    }


    private void addAccount(Account account, Class<? extends Contract> contractType) {
        switch(account.getAccountType()) {

            case ASSET:
                assetAccounts.add(account);
                contractsToAssetAccounts.put(contractType, account);
                break;

            case LIABILITY:
                liabilityAccounts.add(account);
                contractsToLiabilityAccounts.put(contractType, account);
                break;

            case EQUITY:
                equityAccounts.add(account);

            // Not sure what to do with INCOME, EXPENSES
        }

    }

    /**
     * Adding an asset means debiting the account relevant to that type of contract
     * and crediting equity.
     * @param contract an Asset contract to add
     */
    public void addAsset(Contract contract) {
        Account assetAccount = contractsToAssetAccounts.get(contract.getClass());

        if (assetAccount==null) {
            // If there doesn't exist an Account to hold this type of contract, we create it
            assetAccount = new Account(contract.getClass().getName(), AccountType.ASSET);
            addAccount(assetAccount, contract.getClass());
        }

        // (dr asset, cr equity)
        Account.doubleEntry(assetAccount, equityAccount, contract.getValue());

        // Add to the general inventory?
        allAssets.add(contract);
    }

    /**
     * Adding a liability means debiting equity and crediting the account
     * relevant to that type of contract.
     * @param contract a Liability contract to add
     */
    public void addLiability(Contract contract) {
        Account liabilityAccount = contractsToLiabilityAccounts.get(contract.getClass());

        if (liabilityAccount==null) {
            // If there doesn't exist an Account to hold this type of contract, we create it
            liabilityAccount = new Account(contract.getClass().getName(), AccountType.LIABILITY);
            addAccount(liabilityAccount, contract.getClass());
        }

        // (dr equity, cr liability)
        Account.doubleEntry(equityAccount, liabilityAccount, contract.getValue());

        // Add to the general inventory?
        allLiabilities.add(contract);
    }

    public void addCash(double amount) {
        // (dr cash, cr equity)
        Account.doubleEntry(cashAccount, equityAccount, amount);

    }


    /**
     * Operation to cancel a Loan to someone (i.e. cash in a Loan in the Assets side).
     *
     * I'm using this for simplicity but note that this is equivalent to selling an asset.
     * @param amount the amount of loan that is cancelled
     */
    public void pullFunding(double amount, Contract loan) {
        Account loanAccount = contractsToAssetAccounts.get(loan.getClass());

        // (dr cash, cr asset )
        Account.doubleEntry(cashAccount, loanAccount, amount);
    }

    /**
     * Operation to pay back a liability loan; debit liability and credit cash
     * @param amount amount to pay back
     * @param loan the loan which is being paid back
     */
    public void payLiability(double amount, Contract loan) {
        Account liabilityAccount = contractsToLiabilityAccounts.get(loan.getClass());

        assert(getCash() >= amount); // Pre-condition: liquidity has been raised.

        // (dr liability, cr cash )
        Account.doubleEntry(liabilityAccount, cashAccount, amount);


    }

    /**
     * If I've sold an asset, debit cash and credit asset
     * @param amount the *value* of the asset
     */
    public void sellAsset(double amount, Class<? extends Contract> assetType) {
        Account assetAccount = contractsToAssetAccounts.get(assetType);

        // (dr cash, cr asset)
        Account.doubleEntry(cashAccount, assetAccount, amount);
    }

    /**
     * Behavioral stuff; not sure if it should be here
     * @param me the owner of the Ledger
     * @return an ArrayList of Actions that are available to me at this moment
     */
    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = new ArrayList<>();
        for (Contract contract : allAssets) {
            availableActions.addAll(contract.getAvailableActions(me));
        }

        for (Contract contract : allLiabilities) {
            availableActions.addAll(contract.getAvailableActions(me));
        }

        return availableActions;
    }

    /**
     * Stress-testing specific.
     */
    public void updateAssetPrices() {
        List<Contract> allAssets = this.allAssets.stream()
                .filter(contract -> contract instanceof Asset)
                .collect(Collectors.toList());

        for (Contract contract : allAssets) {
            Asset asset = (Asset) contract;
            if (asset.priceFell()) {
                devalueAsset(asset, asset.valueLost());
                asset.updatePrice();
            }
        }
    }

    /**
     * if an Asset loses value, I must debit equity and credit asset
     * @param valueLost the value lost
     */
    public void devalueAsset(Contract asset, double valueLost) {
        Account assetAccount = contractsToAssetAccounts.get(asset.getClass());

        // (dr equityAccounts, cr assetAccounts)
        Account.doubleEntry(equityAccount, assetAccount, valueLost);

        //Todo: perform a check here that the Asset account balances match the value of the assets. (?)
    }

    public void appreciateAsset(Contract asset, double valueLost) {
        Account assetAccount = contractsToAssetAccounts.get(asset.getClass());
        Account.doubleEntry(assetAccount, equityAccount, valueLost);
    }

    public void devalueLiability(Contract liability, double valueLost) {
        Account liabilityAccount = contractsToLiabilityAccounts.get(liability.getClass());

        // (dr equityAccounts, cr assetAccounts)
        Account.doubleEntry(liabilityAccount, equityAccount, valueLost);
    }

    public void appreciateLiability(Contract liability, double valueLost) {
        Account liabilityAccount = contractsToLiabilityAccounts.get(liability.getClass());

        // (dr equityAccounts, cr assetAccounts)
        Account.doubleEntry(equityAccount, liabilityAccount, valueLost);
    }

    /**
     * This mimics the default on a loan. If I lend money to someone and they default on me, at the moment
     * I assume that I lose a 'valueFraction' of its value. There are two double-entry operations:
     *
     * First I take a hit on equity for the lost value of the loan (dr equity, cr asset)
     * Then I cash in the loan (dr cash, cr asset)
     *
     * @param initialValue the original value of the loan
     * @param valueFraction the fraction of the loan that will be lost due to the default
     */
    public void liquidateLoan(double initialValue, double valueFraction, Contract loan) {
        Account assetLoanAccount = contractsToAssetAccounts.get(loan.getClass());

        double valueLost = (1 - valueFraction) * initialValue;

        // First, we devalue the loan
        // (dr equity, cr asset)
        Account.doubleEntry(equityAccount, assetLoanAccount, valueLost);

        // Then, we liquidate it
        // (dr cash, cr asset)
        Account.doubleEntry(cashAccount, assetLoanAccount, initialValue - valueLost);
    }

    public void printBalanceSheet(Agent me) {
        System.out.println("Asset accounts:\n---------------");
        for (Account account : assetAccounts) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        System.out.println("Breakdown: ");
        for (Contract contract : allAssets) {
            System.out.println("\t"+contract.getName(me)+" > "+contract.getValue());
        }
        System.out.println("TOTAL ASSETS: "+ String.format( "%.2f", getAssetValue()));

        System.out.println("\nLiability accounts:\n---------------");
        for (Account account : liabilityAccounts) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        for (Contract contract : allLiabilities) {
            System.out.println("\t"+contract.getName(me)+" > "+contract.getValue());
        }
        System.out.println("TOTAL LIABILITIES: "+ String.format( "%.2f", getLiabilityValue()));
        System.out.println("\nTOTAL EQUITY: "+String.format("%.2f", getEquityValue()));
    }


}
