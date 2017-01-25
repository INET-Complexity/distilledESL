package doubleEntry;

public class OtherAssetsAccount extends Account {
    public OtherAssetsAccount(double startingBalance) {
        super("other_assets",AccountType.ASSET,startingBalance);
    }
    public OtherAssetsAccount() {this(0.0);}
}
