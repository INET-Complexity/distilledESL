package contracts;

import agents.Agent;
import demos.Parameters;

public class AssetCollateral extends Asset implements CanBeCollateral {

    private double encumberedQuantity;


    public AssetCollateral(Agent assetParty, AssetType assetType, AssetMarket assetMarket, double amount) {
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

}
