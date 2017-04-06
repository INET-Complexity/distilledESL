package economicsl.accounting;

import agents.StressAgent;
import contracts.Asset;
import contracts.Repo;
import economicsl.Agent;
import economicsl.Contract;
import economicsl.NotEnoughGoods;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static economicsl.accounting.AccountType.GOOD;

/**
 * This is the main class implementing double entry economicsl.accounting. All public operations provided by this class
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
    protected Agent me;
    protected HashSet<Contract> allAssets;
    protected HashSet<Contract> allLiabilities;
    protected HashMap<String, Double> allGoods;
    protected HashSet<Account> assetAccounts;
    protected HashSet<Account> liabilityAccounts;
    protected HashMap<String, Account> goodsAccounts;
    protected HashSet<Account> equityAccounts;
    protected HashMap<Class<? extends Contract>, Account> contractsToAssetAccounts;
    protected HashMap<Class<? extends Contract>, Account> contractsToLiabilityAccounts;
    protected Account equityAccount;
    private double initialEquity;

    public Ledger(Agent me) {
        contractsToAssetAccounts = new HashMap<>();
        allAssets = new HashSet<>();
        equityAccounts = new HashSet<>();
        allLiabilities = new HashSet<>();
        liabilityAccounts = new HashSet<>();
        goodsAccounts = new HashMap<>();
        allGoods = new HashMap<>();
        contractsToLiabilityAccounts = new HashMap<>();
        this.me = me;
        equityAccount = new Account("equityAccounts", AccountType.EQUITY);
        assetAccounts = new HashSet<>();

        // A StressLedger is a list of accounts (for quicker searching)

        // Each Account includes an inventory to hold one type of contract.
        // These hashmaps are used to access the correct account for a given type of contract.
        // Note that separate hashmaps are needed for asset accounts and liability accounts: the same contract
        // type (such as Loan) can sometimes be an asset and sometimes a liability.

        // A book is initially created with a cash account and an equityAccounts account (it's the simplest possible book)
        addAccount(equityAccount, null);

        allGoods.put("cash", 0.0);
    }

    public double getAssetValue() {
        double assetTotal = 0;
        for (Account assetAccount : assetAccounts) {
            assetTotal += assetAccount.getBalance();
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
                .mapToDouble(contract -> contract.getValue(me))
                .sum();
    }

    public double getLiabilityValueOf(Class<?> contractType) {
//        return contractsToLiabilityAccounts.get(contractType).getBalance();
        return allLiabilities.stream()
                .filter(contractType::isInstance)
                .mapToDouble(contract -> contract.getValue(me))
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

    public double getGood(String name) {
        try {
            return allGoods.get(name);
        } catch (NullPointerException e){
            allGoods.put("name", 0.0);
            return 0.0;
        }
    }

    public double getCash() {
        return getGood("cash");
    }

    protected void addAccount(Account account, Class<? extends Contract> contractType) {
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
        Account.doubleEntry(assetAccount, equityAccount, contract.getValue(me));

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
        Account.doubleEntry(equityAccount, liabilityAccount, contract.getValue(me));

        // Add to the general inventory?
        allLiabilities.add(contract);
    }

    public void addGoods(String name, double amount, double value) {
        assert(amount >= 0.0);
        double have = allGoods.getOrDefault(name, 0.0);
        allGoods.put(name, have + amount);
        Account physicalthingsaccount = getGoodsAccount(name);
        Account.doubleEntry(physicalthingsaccount, equityAccount, amount * value);
    }

    public void subtractGoods(String name, double amount, double value) throws NotEnoughGoods {
        assert(amount >= 0.0);
        double have = getGood(name);
        if (amount > have) {
            throw new NotEnoughGoods(name, have, amount);
        }
        allGoods.put(name, have - amount);
        Account.doubleEntry(equityAccount, getGoodsAccount(name), amount * value);
    }

    public Account getGoodsAccount(String name) {
        Account account = goodsAccounts.get(name);
        if (account == null) {
            account = new Account(name, GOOD);
            goodsAccounts.put(name, account);
        }
        return account;
    }

    public void subtractGoods(String name, double amount) throws NotEnoughGoods {
        try {
            double value = getPhysicalThingValue(name);
            subtractGoods(name, amount, value);
        } catch (NullPointerException e) {
            throw new NotEnoughGoods(name, 0, amount);
        }
    }

    public double getPhysicalThingValue(String name) {
        try {
            return getGoodsAccount(name).getBalance() / getPhysicalThings(name);
        } catch (NullPointerException e) {
            return 0.0;
        }
    }

    private Double getPhysicalThings(String name) {
        return allGoods.get(name);
    }

    /**
     * Reevaluates the current stock of phisical goods at a specified value and books
     * the change to economicsl.accounting
     */
    public void revalueGoods(String name, double value) {
        double old_value = getGoodsAccount(name).getBalance();
        double new_value = allGoods.get(name) * value;
        if (new_value > old_value) {
            Account.doubleEntry(getGoodsAccount(name), equityAccount, new_value - old_value);
        } else if (new_value < old_value) {
            Account.doubleEntry(equityAccount, getGoodsAccount(name), old_value - new_value);
        }
    }

    public void addCash(double amount) {
        // (dr cash, cr equity)
        addGoods("cash", amount, 1.0);
    }

    public void substractCash(double amount) throws NotEnoughGoods {
        subtractGoods("cash", amount, 1.0);
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
        Account.doubleEntry(liabilityAccount, getGoodsAccount("cash"), amount);
    }

    /**
     * If I've sold an asset, debit cash and credit asset
     * @param amount the *value* of the asset
     */
    public void sellAsset(double amount, Class<? extends Contract> assetType) {
        Account assetAccount = contractsToAssetAccounts.get(assetType);

        // (dr cash, cr asset)
        Account.doubleEntry(getGoodsAccount("cash"), assetAccount, amount);
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

    public void printBalanceSheet(StressAgent me) {
        System.out.println("Asset accounts:\n---------------");
        for (Account account : assetAccounts) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }

        System.out.println("Breakdown: ");
        for (Contract contract : allAssets) {
            System.out.println("\t"+contract.getName(me)+" > "+contract.getValue(me));
        }
        System.out.println("TOTAL ASSETS: "+ String.format( "%.2f", getAssetValue()));

        System.out.println("\nLiability accounts:\n---------------");
        for (Account account : liabilityAccounts) {
            System.out.println(account.getName()+" -> "+ String.format( "%.2f", account.getBalance()));
        }
        for (Contract contract : allLiabilities) {
            System.out.println("\t"+contract.getName(me)+" > "+contract.getValue(me));
        }
        System.out.println("TOTAL LIABILITIES: "+ String.format( "%.2f", getLiabilityValue()));
        System.out.println("\nTOTAL EQUITY: "+String.format("%.2f", getEquityValue()));

        System.out.println("\nSummary of encumbered collateral:");
        for (Contract contract : getLiabilitiesOfType(Repo.class)) {
            ((Repo) contract).printCollateral();
        }
        System.out.println("\n\nTotal cash: "+ getGoodsAccount("cash").getBalance());
        System.out.println("Encumbered cash: "+me.getEncumberedCash());
        System.out.println("Unencumbered cash: " + (me.getCash_() - me.getEncumberedCash()));
    }

    public double getInitialEquity() {
        return initialEquity;
    }

    public void setInitialValues() {
        initialEquity = getEquityValue();
    }

    public Account getAccontFromContract(Contract contract) {
        return contractsToAssetAccounts.get(contract.getClass());
    }

    public Account getCashAccount() {
        return getGoodsAccount("cash");
    }
}
