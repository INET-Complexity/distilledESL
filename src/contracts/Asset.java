package contracts;

import agents.Agent;
import actions.Action;
import actions.SellAsset;

import java.util.ArrayList;

public class Asset extends Contract {

    public Asset(Agent assetParty, AssetType assetType, AssetMarket assetMarket, double amount) {
        this.assetParty = assetParty;
        this.assetType = assetType;
        this.assetMarket = assetMarket;
        this.price = assetMarket.getPrice(assetType);
        this.quantity = 1.0 * amount / this.price;
    }

    public Asset(Agent assetParty, AssetType assetType, AssetMarket assetMarket) {
        this(assetParty,assetType, assetMarket, 0.0);
    }


    private Agent assetParty;
    private double quantity;
    private AssetType assetType;
    private AssetMarket assetMarket;
    private double price;

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        if (assetType == AssetType.E) return null; // External assets cannot be sold!

        ArrayList<Action> availableActions = new ArrayList<>();
        if (assetParty == me) {
            availableActions.add(new SellAsset(this));
        }
        return availableActions;
    }

    public void sellAmount(double amount) {
        double quantitySold = 1.0 * amount/price;
        this.quantity -= quantitySold;
        assetMarket.computePriceImpact(assetType, quantitySold);
    }

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return null;
    }

    @Override
    public double getValue() {
        return quantity*price;
    }

    public double getPrice() {
        return assetMarket.getPrice(assetType);
    }

    public boolean priceFell() {
        return (getPrice() < price);
    }

    public double valueLost() {
        return (price-getPrice())*quantity;
    }

    public void updatePrice() {
        price = getPrice();
    }

    public enum AssetType {
        A1, A2, A3, E
    }

    public AssetType getAssetType() {
        return assetType;
    }
}

