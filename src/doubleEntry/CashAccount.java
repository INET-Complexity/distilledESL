package doubleEntry;

public class CashAccount extends Account {
    public CashAccount(double startingBalance) {
        super("cash",AccountType.ASSET,startingBalance);
    }
    public CashAccount() {this(0.0);}
 }
