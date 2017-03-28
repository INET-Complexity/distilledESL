package accounting;

class Account {

    private double balance;
    private AccountType accountType;
    private String name;

    private Account(String name, AccountType accountType, Double startingBalance) {
        this.name = name;
        this.accountType = accountType;
        this.balance = startingBalance;
    }

    Account(String name, AccountType accountType) {
        this(name, accountType, 0.0);
    }

    static void doubleEntry(Account debitAccount, Account creditAccount, double amount) {
        debitAccount.debit(amount);
        creditAccount.credit(amount);
    }

    /**
     * A Debit is a positive change for ASSET and EXPENSES accounts, and negative for the rest.
     *
     * @param amount the amount to debit
     */
    private void debit(double amount) {
        if ((accountType == AccountType.ASSET) || (accountType == AccountType.EXPENSES)) {
            balance += amount;
        } else {
            balance -= amount;
        }
    }

    /**
     * A Credit is a negative change for ASSET and EXPENSES accounts, and positive for the rest.
     *
     * @param amount the amount to credit
     */
    private void credit(double amount) {
        if ((accountType == AccountType.ASSET) || (accountType == AccountType.EXPENSES)) {
            balance -= amount;
        } else {
            balance += amount;
        }
    }

    AccountType getAccountType() {
        return accountType;
    }

    double getBalance() {
        return balance;
    }

    String getName() {
        return name;
    }
}
