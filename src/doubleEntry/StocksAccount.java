package doubleEntry;

public class StocksAccount extends Account {
    public StocksAccount(double startingBalance) {
        super("stocks",AccountType.ASSET,startingBalance);
    }
    public StocksAccount() {this(0.0);}
}
