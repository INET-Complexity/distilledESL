package doubleEntry;

public class ReverseRepoAccount extends Account {
    public ReverseRepoAccount(double startingBalance) {
        super("reverse_repo",AccountType.ASSET,startingBalance);
    }
    public ReverseRepoAccount() {this(0.0);}
}
