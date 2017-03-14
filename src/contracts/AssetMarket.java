package contracts;

import demos.Parameters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AssetMarket {
    private HashMap<Asset.AssetType, Double> prices;
    private HashMap<Asset.AssetType, Double> priceImpacts;
    private HashMap<Asset.AssetType, Double> amountsSold;
    private HashSet<Order> orderbook;

    public AssetMarket() {
        prices = new HashMap<>();
        priceImpacts = new HashMap<>();
        amountsSold = new HashMap<>();
        orderbook = new HashSet<>();

        init();

    }

    private void init() {
        setPrice(Asset.AssetType.CORPORATE_BONDS, 1.0);
        setPrice(Asset.AssetType.EQUITIES, 1.0);
        setPrice(Asset.AssetType.EXTERNAL, 1.0);
        setPrice(Asset.AssetType.MBS, 1.0);

        priceImpacts.put(Asset.AssetType.MBS, Parameters.PRICE_IMPACT_MBS);
        priceImpacts.put(Asset.AssetType.EQUITIES, Parameters.PRICE_IMPACT_EQUITIES);
        priceImpacts.put(Asset.AssetType.CORPORATE_BONDS, Parameters.PRICE_IMPACT_CORPORATE_BONDS);

    }

    public void putForSale(Asset asset, double amount) {
        orderbook.add(new Order(asset, amount));
        Asset.AssetType type = asset.getAssetType();

        if (!amountsSold.containsKey(type)) {
            amountsSold.put(type, amount);
        } else {
            amountsSold.put(type, amountsSold.get(type) + amount);
        }
    }

    public void clearTheMarket() {
        System.out.println("\nMARKET CLEARING\n");
        for (Map.Entry<Asset.AssetType, Double> entry : amountsSold.entrySet()) {
            computePriceImpact(entry.getKey(), entry.getValue());
        }

        amountsSold.clear();

        for (Order order : orderbook) {
            order.settle();
        }

        orderbook.clear();
    }


    private void computePriceImpact(Asset.AssetType assetType, double amountSold) {
        double newPrice = prices.get(assetType) * (1.0 - amountSold * priceImpacts.get(assetType));
        setPrice(assetType, newPrice);
    }

    private void computeHaircuts(Asset.AssetType assetType, double amountSold) {
        //TODO
    }

    public double getPrice(Asset.AssetType assetType) {
        return prices.get(assetType);
    }

    private void setPrice(Asset.AssetType assetType, double newPrice) {
        prices.put(assetType, newPrice);
    }

    public void shockPrice(Asset.AssetType assetType, double fraction) {
        setPrice(assetType, getPrice(assetType) * (1.0 - fraction));
    }


    private class Order {
        private Asset asset;
        private double quantity;

        private Order(Asset asset, double quantity) {
            this.asset = asset;
            this.quantity = quantity;
        }

        private void settle() {
            asset.clearSale(quantity);
        }

    }
}
