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
        if (assetType == AssetType.EXTERNAL) return null; // External assets cannot be sold!

        ArrayList<Action> availableActions = new ArrayList<>();
        if (assetParty == me) {
            availableActions.add(new SellAsset(this));
        }
        return availableActions;
    }

    public void putForSale(double quantity) {
        assetMarket.putForSale(this, quantity);
    }

    /**
     * We had an amount Q of asset valued at price P. We sold an amount q at price p.
     *
     * 1) We gain an amount p*q of cash.
     * 2) We make a loss Q*(P-p) in equity due to the devaluation.
     * @param quantitySold the quantity of asset sold, in units
     */
    public void clearSale(double quantitySold) {
        double newPrice = getMarketPrice();
        assetParty.sellAssetForValue(this, quantitySold * newPrice);
        // Take the loss on devaluation.
        if (newPrice < price) {
            assetParty.devalueAsset(this, valueLost());
        }
        // Update the quantity remaining
        this.quantity -= quantitySold;
        // Update the price
        updatePrice();
    }


    @Override
    public double getValue() {
        return quantity*price;
    }

    public double getPrice() {
        return price;
    }
    private double getMarketPrice() {
        return assetMarket.getPrice(assetType);
    }

    public boolean priceFell() {
        return (getMarketPrice() < price);
    }

    public double valueLost() {
        return (price-getMarketPrice())*quantity;
    }

    public void updatePrice() {
        price = getMarketPrice();
    }

    public enum AssetType {
        MBS,
        EQUITIES,
        CORPORATE_BONDS,
        EXTERNAL
    }

    public AssetType getAssetType() {
        return assetType;
    }

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return null;
    } //An Asset does not have a liability party

    protected double getQuantity() {
        return quantity;
    }
}

