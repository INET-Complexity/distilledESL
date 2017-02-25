package accounting;

import agents.Agent;
import actions.Action;
import contracts.Asset;
import contracts.Contract;
import contracts.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the main class implementing double entry accounting. All public operations provided by this class
 * are performed as a double entry operation, i.e. a pair of (dr, cr) operations.
 *
 * A Book contains a set of accounts, and is the interface between an agent and its accounts. Agents cannot
 * directly interact with accounts other than via a Book.
 *
 * At the moment, a Book contains an account for each type of contract.
 *
 * A simple economic agent will usually have a single Book, whereas complex firms and banks can have several.
 */
public class Book {

    public Book(Agent owner) {
        // A Book is a list of accounts
        accounts = new HashSet<>();

        // Subsets of the list of accounts (for quicker searching)
        assets = new HashSet<>();
        liabilities = new HashSet<>();
        equity = new HashSet<>();

        // Each Account includes an inventory to hold one type of contract.
        // These hashmaps are used to access the correct account for a given type of contract.
        // Note that separate hashmaps are needed for asset accounts and liability accounts: the same contract
        // type (such as Loan) can be an asset or a liability so might have to be placed on an asset or a liability account.
        assetAccountsMap = new HashMap<>();
        liabilityAccountsMap = new HashMap<>();

        // Not sure whether I should be passing the owner
        this.owner = owner;

        // A book is initially created with a cash account and an equity account (it's the simplest possible book)
        cashAccount = new Account("cash", AccountType.ASSET);
        equityAccount = new Account("equity", AccountType.EQUITY);
    }


    private Agent owner;
    private HashSet<Account> accounts;
    private HashSet<Account> assets;
    private HashSet<Account> liabilities;
    private HashSet<Account> equity;
    private HashMap<Class<? extends contracts.Contract>,Account> assetAccountsMap;
    private HashMap<Class<? extends contracts.Contract>,Account> liabilityAccountsMap;
    private Account cashAccount;
    private Account equityAccount;

    public double getAssetValue() {
        double assetTotal = 0;
        for (Account assetAccount : assets) {
            assetTotal+=assetAccount.getBalance();
        }
        return assetTotal;
    }


    public double getLiabilityValue() {
        double liabilityTotal = 0;
        for (Account liabilityAccount : liabilities) {
            liabilityTotal+=liabilityAccount.getBalance();
        }
        return liabilityTotal;
    }

    public double getEquityValue() {
        double equityTotal = 0;
        for (Account equityAccount : equity) {
            equityTotal += equityAccount.getBalance();
        }
        return equityTotal;
    }

    public double getCash() {
        return cashAccount.getBalance();
    }


    private void addAccount(Account account, Class<? extends Contract> contractType) {
        accounts.add(account);
        switch(account.getAccountType()) {

            case ASSET:
                assets.add(account);
                assetAccountsMap.put(contractType, account);
                break;

            case LIABILITY:
                liabilities.add(account);
                liabilityAccountsMap.put(contractType, account);
                break;

            // Not sure what to do with EQUITY, INCOME, EXPENSES
        }

    }

    /**
     * Adding an asset means debiting the assets account relevant to that type of contract and crediting the equity account.
     * @param contract an Asset contract to add
     */
    public void addAsset(Contract contract) {
        Account assetAccount = assetAccountsMap.get(contract.getClass());

        if (assetAccount==null) {
            // If there doesn't exist an Account to hold this type of contract, we create it
            assetAccount = new Account(contract.getClass().getName(), AccountType.ASSET);
            addAccount(assetAccount, contract.getClass());
        }

        // (dr assets, cr equity)
        assetAccount.debit(contract.getValue());
        equityAccount.credit(contract.getValue());

        // Add the contract to the account's inventory
        assetAccount.addContract(contract);
    }

    /**
     * Adding a liability means debiting equity and crediting the liabilities account relevant to that type of contract.
     * @param contract a Liability contract to add
     */
    public void addLiability(Contract contract) {
        Account liabilityAccount = liabilityAccountsMap.get(contract.getClass());


        if (liabilityAccount==null) {
            // If there doesn't exist an Account to hold this type of contract, we create it
            liabilityAccount = new Account(contract.getClass().getName(), AccountType.LIABILITY);
            addAccount(liabilityAccount, contract.getClass());
        }

        // (dr equity, cr liability)
        equityAccount.debit(contract.getValue());
        liabilityAccount.credit(contract.getValue());

        liabilityAccount.addContract(contract);
    }

    public void addCash(double amount) {

        // (dr assets, cr equity)
        cashAccount.debit(amount);
        equityAccount.credit(amount);
    }


    /**
     * Operation to cancel a Loan to someone (i.e. cash in a Loan in the Assets side).
     *
     * I'm using this for simplicity but note that this is equivalent to selling an asset.
     * @param amount the amount of loan that is cancelled
     */
    public void pullFunding(double amount) {
        Account loanAccount = assetAccountsMap.get(Loan.class);

        // (dr cash, cr assets (loan) )
        cashAccount.debit(amount);
        loanAccount.credit(amount);

    }

    /**
     * Operation to pay back a liability loan; debit liabilities and credit cash
     * @param amount amount to pay back
     */
    public void payLiability(double amount, Class<? extends Contract> liabilityType) {
        Account liabilityAccount = liabilityAccountsMap.get(liabilityType);

        //Todo: What do we do if we can't pay??!! At the moment I'm calling my owner to raise liquidity
        if (cashAccount.getBalance() < amount) {
            System.out.println();
            System.out.println("***");
            System.out.println(owner.getName()+" must raise liquidity immediately.");
            owner.raiseLiquidity(amount * (1 - cashAccount.getBalance()/getAssetValue()));
            System.out.println("***");
            System.out.println();
        }

        // (dr liabilities, cr cash )
        liabilityAccount.debit(amount);
        cashAccount.credit(amount);

    }

    /**
     * If I've sold an asset, debit cash and credit assets
     * @param amount the *value* of the asset
     */
    public void sellAsset(double amount, Class<? extends Contract> assetType) {
        Account assetAccount = assetAccountsMap.get(assetType);

        // (dr cash, cr assets)
        cashAccount.debit(amount);
        assetAccount.credit(amount);
    }

    /**
     * Behavioral stuff
     * @param me the owner of the Book
     * @return an ArrayList of Actions that are available to me at this moment
     */
    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = new ArrayList<>();
        for (Account account : accounts) {
            availableActions.addAll(account.getAvailableActions(me));
        }
        return availableActions;
    }

    /**
     * Stress-testing specific.
     */
    public void updateAssetPrices() {
        Account assetAccount = assetAccountsMap.get(Asset.class);

        for (Contract contract : assetAccount.contracts) {
            Asset asset = (Asset) contract;
            if (asset.priceFell()) {
                devalueAsset(asset.valueLost());
                asset.updatePrice();
            }
        }
    }

    /**
     * if an Asset loses value, I must debit equity and credit assets
     * @param amount
     */
    private void devalueAsset(double amount) {
        Account assetAccount = assetAccountsMap.get(Asset.class);

        // (dr equity, cr assets)
        equityAccount.debit(amount);
        assetAccount.credit(amount);
    }

    /**
     * This mimics the default on a loan. If I lend money to someone and they default on me, at the moment
     * I assume that I lose a 'valueFraction' of its value. There are two double-entry operations:
     *
     * First I take a hit on equity for the lost value of the loan (dr equity, cr assets)
     * Then I cash in the loan (dr cash, cr assets)
     *
     * @param initialValue the original value of the loan
     * @param valueFraction the fraction of the loan that will be lost due to the default
     */
    public void liquidateLoan(double initialValue, double valueFraction) {
        Account assetLoanAccount = assetAccountsMap.get(Loan.class);

        double valueLost = (1 - valueFraction) * initialValue;

        // First, we devalue the loan :(
        // (dr equity, cr asset)
        equityAccount.debit(valueLost);
        assetLoanAccount.credit(valueLost);

        // Then, we liquidate it
        // (dr cash, cr asset)
        cashAccount.debit(initialValue-valueLost);
        assetLoanAccount.credit(initialValue-valueLost);
    }

    public void printBalanceSheet() {
        System.out.println("Asset accounts:");
        System.out.println("---------------");
        for (Account account : assets) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        System.out.println("TOTAL ASSETS: "+ String.format( "%.2f", getAssetValue()));
        System.out.println();

        System.out.println("Liability accounts:");
        System.out.println("---------------");
        for (Account account : liabilities) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        System.out.println("TOTAL LIABILITIES: "+ String.format( "%.2f", getLiabilityValue()));
        System.out.println();
        System.out.println("TOTAL EQUITY: "+String.format("%.2f", getEquityValue()));
        System.out.println();
    }



}
