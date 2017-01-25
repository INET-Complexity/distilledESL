package doubleEntry;

import components.items.Bond;

public class BondAssetAccount extends Account {

    public BondAssetAccount(double startingBalance) {
        super("bond_asset",AccountType.ASSET,startingBalance);
        setContractClass(doubleEntryComponents.contracts.Bond.class);
    }
    public BondAssetAccount() {this(0.0);}
}
