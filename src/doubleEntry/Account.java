package doubleEntry;

import components.items.*;

public class Account {

    public Account(String name, AccountType accountType, Double startingBalance) {
        this.name = name;
        this.accountType = accountType;
        this.total = startingBalance;
    }

    private double total;

    private Collateral collateralType;
    private AccountType accountType;
    private String name;


    public void debit(double amount) {
        if (accountType==AccountType.ASSET) {
            total += amount;
        } else {
            total -= amount;
        }
    }


    public void credit(double amount) {
        if (accountType==AccountType.ASSET) {
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
}
