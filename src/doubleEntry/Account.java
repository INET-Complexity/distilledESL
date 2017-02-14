package doubleEntry;

import doubleEntryComponents.Agent;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.contracts.Contract;

import java.util.ArrayList;
import java.util.HashSet;

public class Account {

    public Account(String name, AccountType accountType, Double startingBalance) {
        this.name = name;
        this.accountType = accountType;
        this.total = startingBalance;
        this.contractClass = null;
        this.contracts = new HashSet<>();
    }

    public Account(String name, AccountType accountType) {
        this(name,accountType,0.0);
    }

    private double total;

    private Collateral collateralType;
    private AccountType accountType;
    private String name;
    private Class<? extends Contract> contractClass;
    protected HashSet<Contract> contracts;


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


    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = new ArrayList<>();
        for (Contract contract : contracts) {
            ArrayList<Action> contractActions = contract.getAvailableActions(me);
            if (contractActions != null) availableActions.addAll(contractActions);
        }
        return availableActions;
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

    public String getName() {
        return name;
    }
}
