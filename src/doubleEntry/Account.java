package doubleEntry;

import doubleEntryComponents.contracts.Contract;

import java.util.HashSet;

public abstract class Account {

    Account(String name, AccountType accountType, Double startingBalance) {
        this.name = name;
        this.accountType = accountType;
        this.total = startingBalance;
        this.contractClass = null;
        this.contracts = new HashSet<>();
    }

    Account(String name, AccountType accountType) {
        this(name,accountType,0.0);
    }

    private double total;

    private Collateral collateralType;
    private AccountType accountType;
    private String name;
    private Class<? extends Contract> contractClass;
    private HashSet<Contract> contracts;


    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    /**
     * A Debit is a positive change for ASSET and EXPENSES accounts, and negative for the rest.
     * @param amount the amount to debit
     */
    public void debit(double amount) {
        if ((accountType==AccountType.ASSET) || (accountType==AccountType.EXPENSES)) {
            total += amount;
        } else {
            total -= amount;
        }
    }

    /**
     * A Credit is a negative change for ASSET and EXPENSES accounts, and positive for the rest.
     * @param amount the amount to credit
     */
    public void credit(double amount) {
        if ((accountType==AccountType.ASSET) || (accountType==AccountType.EXPENSES)) {
            total -= amount;
        } else {
            total += amount;
        }
    }



    public void setCollateralType(Collateral collateralType) {
        this.collateralType = collateralType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getTotal() {
        return total;
    }

    public void setContractClass(Class<? extends Contract> contractClass) {
        this.contractClass = contractClass;
    }

    public Class<? extends Contract> getContractClass() {
        return contractClass;
    }
}
