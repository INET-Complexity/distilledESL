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
 *
 * @author rafa
 */
public class Book implements BookAPI {

    public Book(Agent owner) {
        // A Book is a list of accounts
        accounts = new HashSet<>();

        // Subsets of the list of accounts (for quicker searching)
        assetAccounts = new HashSet<>();
        liabilityAccounts = new HashSet<>();
        equityAccounts = new HashSet<>();

        // Each Account includes an inventory to hold one type of contract.
        // These hashmaps are used to access the correct account for a given type of contract.
        // Note that separate hashmaps are needed for asset accounts and liability accounts: the same contract
        // type (such as Loan) can be an asset or a liability so might have to be placed on an asset or a liability account.
        assetAccountsMap = new HashMap<>();
        liabilityAccountsMap = new HashMap<>();

        // Not sure whether I should be passing the owner
        this.owner = owner;

        // A book is initially created with a cash account and an equityAccounts account (it's the simplest possible book)
        cashAccount = new Account("cash", AccountType.ASSET);
        equityAccount = new Account("equityAccounts", AccountType.EQUITY);
        addAccount(cashAccount, null);
        addAccount(equityAccount, null);
    }


    private Agent owner;
    private HashSet<Account> accounts;
    private HashSet<Account> assetAccounts;
    private HashSet<Account> liabilityAccounts;
    private HashSet<Account> equityAccounts;
    private HashMap<Class<? extends contracts.Contract>, Account> assetAccountsMap;
    private HashMap<Class<? extends contracts.Contract>, Account> liabilityAccountsMap;
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

    public double getAssetValueOf(Class<? extends Contract> contractType) {
        return assetAccountsMap.get(contractType).getBalance();
    }

    public double getLiabilityValueOf(Class<? extends Contract> contractType) {
        return liabilityAccountsMap.get(contractType).getBalance();
    }

    public double getCash() {
        return cashAccount.getBalance();
    }


    private void addAccount(Account account, Class<? extends Contract> contractType) {
        accounts.add(account);
        switch(account.getAccountType()) {

            case ASSET:
                assetAccounts.add(account);
                assetAccountsMap.put(contractType, account);
                break;

            case LIABILITY:
                liabilityAccounts.add(account);
                liabilityAccountsMap.put(contractType, account);
                break;

            case EQUITY:
                equityAccounts.add(account);

            // Not sure what to do with INCOME, EXPENSES
        }

    }

    /**
     * Adding an asset means debiting the assetAccounts account relevant to that type of contract and crediting the equityAccounts account.
     * @param contract an Asset contract to add
     */
    public void addAsset(Contract contract) {
        Account assetAccount = assetAccountsMap.get(contract.getClass());

        if (assetAccount==null) {
            // If there doesn't exist an Account to hold this type of contract, we create it
            assetAccount = new Account(contract.getClass().getName(), AccountType.ASSET);
            addAccount(assetAccount, contract.getClass());
        }

        // (dr assetAccounts, cr equityAccounts)
        assetAccount.debit(contract.getValue());
        equityAccount.credit(contract.getValue());

        // Add the contract to the account's inventory
        assetAccount.addContract(contract);
    }

    /**
     * Adding a liability means debiting equityAccounts and crediting the liabilityAccounts account relevant to that type of contract.
     * @param contract a Liability contract to add
     */
    public void addLiability(Contract contract) {
        Account liabilityAccount = liabilityAccountsMap.get(contract.getClass());


        if (liabilityAccount==null) {
            // If there doesn't exist an Account to hold this type of contract, we create it
            liabilityAccount = new Account(contract.getClass().getName(), AccountType.LIABILITY);
            addAccount(liabilityAccount, contract.getClass());
        }

        // (dr equityAccounts, cr liability)
        equityAccount.debit(contract.getValue());
        liabilityAccount.credit(contract.getValue());

        liabilityAccount.addContract(contract);
    }

    public void addCash(double amount) {

        // (dr assetAccounts, cr equityAccounts)
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

        // (dr cash, cr assetAccounts (loan) )
        cashAccount.debit(amount);
        loanAccount.credit(amount);

    }

    /**
     * Operation to pay back a liability loan; debit liabilityAccounts and credit cash
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

        // (dr liabilityAccounts, cr cash )
        liabilityAccount.debit(amount);
        cashAccount.credit(amount);

    }

    /**
     * If I've sold an asset, debit cash and credit assetAccounts
     * @param amount the *value* of the asset
     */
    public void sellAsset(double amount, Class<? extends Contract> assetType) {
        Account assetAccount = assetAccountsMap.get(assetType);

        // (dr cash, cr assetAccounts)
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
     * if an Asset loses value, I must debit equityAccounts and credit assetAccounts
     * @param amount
     */
    private void devalueAsset(double amount) {
        Account assetAccount = assetAccountsMap.get(Asset.class);

        // (dr equityAccounts, cr assetAccounts)
        equityAccount.debit(amount);
        assetAccount.credit(amount);
    }

    /**
     * This mimics the default on a loan. If I lend money to someone and they default on me, at the moment
     * I assume that I lose a 'valueFraction' of its value. There are two double-entry operations:
     *
     * First I take a hit on equityAccounts for the lost value of the loan (dr equityAccounts, cr assetAccounts)
     * Then I cash in the loan (dr cash, cr assetAccounts)
     *
     * @param initialValue the original value of the loan
     * @param valueFraction the fraction of the loan that will be lost due to the default
     */
    public void liquidateLoan(double initialValue, double valueFraction) {
        Account assetLoanAccount = assetAccountsMap.get(Loan.class);

        double valueLost = (1 - valueFraction) * initialValue;

        // First, we devalue the loan :(
        // (dr equityAccounts, cr asset)
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
        for (Account account : assetAccounts) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        System.out.println("TOTAL ASSETS: "+ String.format( "%.2f", getAssetValue()));
        System.out.println();

        System.out.println("Liability accounts:");
        System.out.println("---------------");
        for (Account account : liabilityAccounts) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        System.out.println("TOTAL LIABILITIES: "+ String.format( "%.2f", getLiabilityValue()));
        System.out.println();
        System.out.println("TOTAL EQUITY: "+String.format("%.2f", getEquityValue()));
        System.out.println();
    }



}
