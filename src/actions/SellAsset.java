package actions;

import contracts.Asset;
import contracts.AssetCollateral;

public class SellAsset extends Action {

    private Asset asset;

    public SellAsset(Asset asset) {
        this.asset = asset;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        double quantityToSell = getAmount() / asset.getPrice();
        asset.putForSale(quantityToSell);
    }

    @Override
    public double getMax() {
        if (asset instanceof AssetCollateral) {
            // Only unencumbered assets can be sold!
            return ((AssetCollateral)asset).getUnencumberedValue() - asset.getPutForSale();
        } else {
            return asset.getValue();
        }
    }
    //Todo: should this be here, or should it be with the AssetCollateral?

    @Override
    public void print() {
        System.out.println("Sell Asset action by "+asset.getAssetParty().getName()+" -> asset type: "+asset.getAssetType() +", amount: "+String.format( "%.2f", getAmount()));
    }

    public String getName() {
        return "Sell Asset of type "+ asset.getAssetType()+" [max: "+getMax()+"]";
    }
}
