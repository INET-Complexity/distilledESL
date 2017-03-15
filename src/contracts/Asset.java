package contracts;

import agents.Agent;
import actions.Action;
import actions.SellAsset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    protected AssetMarket assetMarket;
    private double price;

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(assetParty==me) || !(quantity >0) || assetType==AssetType.EXTERNAL) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        availableActions.add(new SellAsset(this));
        return availableActions;
    }

    public void putForSale(double quantity) {
        assetMarket.putForSale(this, quantity);
    }

    /**
     * We had an amount Q of asset valued at price P. The we sold a quantity q that made the price fall to p. The
     * sale happened at the mid-point price (P+p)/2.
     *
     * 1) We gain an amount pq of cash.
     * 2) We make a loss q(P-p)/2 from the sale, and a loss (Q-q)*(P-p) due to the devaluation.
     * @param quantitySold the quantity of asset sold, in units
     */
    protected void clearSale(double quantitySold) {
        double newPrice = getMarketPrice();
        // Sell the asset at the mid-point price
        assetParty.sellAssetForValue(this, quantitySold * 0.5 * (price + newPrice));

        // Take the loss on devaluation.
        if (newPrice < price) {
            // Value lost is the sum of the value lost from the transaction and the devaluation of the asset that is left.
            double totalValueLost = quantitySold * 0.5 * (price - newPrice) + (quantity - quantitySold) * (price - newPrice);
            assetParty.devalueAsset(this, totalValueLost);
        }

        // Update the quantity remaining
        this.quantity -= quantitySold;

        // Update the price
        updatePrice();
    }


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

