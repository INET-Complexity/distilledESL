package contracts;

import java.util.HashMap;

public class AssetMarket {
    private HashMap<Asset.AssetType, Double> prices;
    private HashMap<Asset.AssetType, Double> priceImpacts;

    public AssetMarket() {
        prices = new HashMap<>();
        setPrice(Asset.AssetType.CORPORATE_BONDS, 1.0);
        setPrice(Asset.AssetType.EQUITIES, 1.0);
        setPrice(Asset.AssetType.EXTERNAL, 1.0);
        setPrice(Asset.AssetType.MBS, 1.0);
    }


    private void computePriceImpact(Asset.AssetType assetType, double amountSold) {
        double newPrice = prices.get(assetType) * (1.0 - amountSold * priceImpacts.get(assetType));
        setPrice(assetType, newPrice);
    }

    public double getPrice(Asset.AssetType assetType) {
        return prices.get(assetType);
    }

    private void setPrice(Asset.AssetType assetType, double newPrice) {
        prices.put(assetType, newPrice);
    }
}
