package contracts;

import demos.Parameters;

import java.util.*;

public class AssetMarket {
    private HashMap<Asset.AssetType, Double> prices;
    private HashMap<Asset.AssetType, Double> priceImpacts;
    private HashMap<Asset.AssetType, Double> amountsSold;
    private HashMap<Asset.AssetType, Double> haircuts; //Todo: at the moment, haircuts are set at the AssetMarket.
    private HashSet<Order> orderbook;

    public AssetMarket() {
        prices = new HashMap<>();
        priceImpacts = new HashMap<>();
        amountsSold = new HashMap<>();
        haircuts = new HashMap<>();
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

        haircuts.put(Asset.AssetType.MBS, Parameters.HAIRCUT_MBS);
        haircuts.put(Asset.AssetType.EQUITIES, Parameters.HAIRCUT_EQUITIES);
        haircuts.put(Asset.AssetType.CORPORATE_BONDS, Parameters.HAIRCUT_CORPORATE_BONDS);

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
            if (Parameters.FIRESALE_CONTAGION) computePriceImpact(entry.getKey(), entry.getValue());
            if (Parameters.HAIRCUT_CONTAGION) computeHaircut(entry.getKey(), entry.getValue());
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

    private void computeHaircut(Asset.AssetType assetType, double amountSold) {
        if (!haircuts.containsKey(assetType)) return;

        double h0 = Parameters.getInitialHaircut(assetType);
        double p0 = 1.0; // Todo: is the initial price always 1.0?

        double newHaircut = h0 * Parameters.HAIRCUT_SLOPE * Math.max((p0 - getPrice(assetType)) / p0, 0.0);
        haircuts.put(assetType, newHaircut);

    }

    public double getPrice(Asset.AssetType assetType) {
        return prices.get(assetType);
    }

    public double getHaircut(Asset.AssetType assetType) {
        return haircuts.containsKey(assetType) ? haircuts.get(assetType) : 0.0;
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

    public ArrayList<Asset.AssetType> getAssetTypes() {
        ArrayList<Asset.AssetType> assetTypesArray = new ArrayList<>();

        Set<Asset.AssetType> assetTypes = prices.keySet();
        for (Asset.AssetType type : assetTypes) {
            assetTypesArray.add(type);
        }

        return assetTypesArray;
    }
}
