package contracts;

import agents.StressAgent;
import actions.Action;
import actions.SellAsset;
import agents.Agent;
import demos.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Asset extends Contract {

    public Asset(StressAgent assetParty, AssetType assetType, AssetMarket assetMarket, double quantity) {
        this.assetParty = assetParty;
        this.assetType = assetType;
        this.assetMarket = assetMarket;
        this.price = assetMarket.getPrice(assetType);
        this.quantity = quantity;
        this.putForSale = 0.0;
    }

    public Asset(StressAgent assetParty, AssetType assetType, AssetMarket assetMarket) {
        this(assetParty,assetType, assetMarket, 0.0);
    }

    @Override
    public String getName(Agent me) {
        return "Asset of type "+assetType;
    }

    private StressAgent assetParty;
    protected double quantity;
    private AssetType assetType;
    protected AssetMarket assetMarket;
    private double price;
    private double putForSale;

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(assetParty==me) || !(quantity > putForSale)
                || (assetType==AssetType.EXTERNAL1)
                || (assetType==AssetType.EXTERNAL2)
                || (assetType==AssetType.EXTERNAL3)) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        availableActions.add(new SellAsset(me, this));
        return availableActions;
    }

    public void putForSale(double quantity) {
        putForSale += quantity;
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

        // Take the loss on the sale.
        if (newPrice < price) {
            // Value lost is the value lost from the transaction.
            double totalValueLost = quantitySold * 0.5 * (price - newPrice);

//            double totalValueLost = quantitySold * 0.5 * (price - newPrice) + (quantity - quantitySold) * (price - newPrice);
            System.out.println(assetParty.getName() + " made a loss of " + String.format("%.2f", totalValueLost) + " from the sale of " + getAssetType());
            assetParty.devalueAsset(this, totalValueLost);
        }

        // Update the quantity remaining
        assert(quantitySold <= quantity);
        this.quantity -= quantitySold;
        this.putForSale -= quantitySold;

        // Update the price
        updatePrice();
    }


    public double getValue(Agent me) {
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
        EXTERNAL1,
        EXTERNAL2,
        EXTERNAL3
        //TODO: mark the three external assets as non tradable.
    }

    public AssetType getAssetType() {
        return assetType;
    }

    @Override
    public StressAgent getAssetParty() {
        return assetParty;
    }

    @Override
    public StressAgent getLiabilityParty() {
        return null;
    } //An Asset does not have a liability party

    public double getQuantity() {
        return quantity;
    }

    public double getPutForSale() {
        return putForSale;
    }

    @Override
    public double getRWAweight() {
        return Parameters.getRWAWeight(assetType);
    }


}

