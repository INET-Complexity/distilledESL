package doubleEntryComponents.contracts;

public class AssetMarket {
    private double price1;
    private double price2;
    private double price3;
    private double priceE;
    private static final double PRICE_IMPACT = 0.0025;

    public AssetMarket() {
        price1=1;
        price2=1;
        price3=1;
        priceE=1;
    }

    public void computePriceImpact(Asset.AssetType assetType, double amount) {
        double currentPrice = getPrice(assetType);
        double newPrice = currentPrice * (1.0 - amount*PRICE_IMPACT);
        switch (assetType) {
            case A1:
                price1 = newPrice;
                break;
            case A2:
                price2 = newPrice;
                break;
            case A3:
                price3 = newPrice;
                break;
            case E:
                priceE = newPrice;
                break;
        }
    }

    public double getPrice(Asset.AssetType assetType) {
        double price = 0;
        switch (assetType) {
            case A1:
                price=price1;
                break;

            case A2:
                price=price2;
                break;

            case A3:
                price=price3;
                break;

            case E:
                price=priceE;
                break;
        }

        return price;
    }

    public void setPrice1(double price1) {
        this.price1 = price1;
    }

    public void setPrice2(double price2) {
        this.price2 = price2;
    }

    public void setPrice3(double price3) {
        this.price3 = price3;
    }

    public void setPriceE(double priceE) {
        this.priceE = priceE;
    }
}
