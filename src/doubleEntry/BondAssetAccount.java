package doubleEntry;

import components.items.Bond;

public class BondAssetAccount extends Account {

    public BondAssetAccount(double startingBalance) {
        super("bond_asset",AccountType.ASSET,startingBalance);
    }

}
