package contracts;

import agents.StressAgent;

public class AssetCollateral extends Asset implements CanBeCollateral {

    private double encumberedQuantity;


    public AssetCollateral(StressAgent assetParty, AssetType assetType, AssetMarket assetMarket, double amount) {
        super(assetParty, assetType, assetMarket, amount);
    }

    @Override
    public void encumber(double quantity) {
        encumberedQuantity += quantity;
    }

    @Override
    public void unEncumber(double quantity) {
        encumberedQuantity -= quantity;
    }

    @Override
    public double getHaircut() {
        return assetMarket.getHaircut(getAssetType());
    }

    public double getUnencumberedQuantity() {
        return getQuantity() - encumberedQuantity;
    }

    public double getUnencumberedValue() {return getUnencumberedQuantity() * getPrice();}

    @Override
    public AssetCollateral changeOwnership(StressAgent newOwner, double quantity) {
        assert(this.quantity >= quantity);

        // First, reduce the quantity of this asset
        this.quantity -= quantity;
        this.encumberedQuantity -= quantity;

        // Have the owner lose the value of the asset
        getAssetParty().devalueAsset(this, quantity * getPrice());

        AssetCollateral newAsset = new AssetCollateral(newOwner, getAssetType(), assetMarket, quantity);

        return newAsset;
    }
}
