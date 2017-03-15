package demos;

import contracts.Asset;

public class Parameters {
    public static double HAIRCUT_MBS = 0.1;
    public static double HAIRCUT_CORPORATE_BONDS = 0.1;
    public static double HAIRCUT_EQUITIES = 0.1;

    public static double HAIRCUT_SLOPE = 0.1;

    public static double PRICE_IMPACT_MBS = 0.001;
    public static double PRICE_IMPACT_CORPORATE_BONDS = 0.001;
    public static double PRICE_IMPACT_EQUITIES = 0.001;

    public static double ASSET_MANAGER_LIQUIDITY_FRACTION = 0.05;


    public static double getInitialHaircut(Asset.AssetType assetType) {
        switch (assetType) {
            case CORPORATE_BONDS:
                return HAIRCUT_CORPORATE_BONDS;
            case EQUITIES:
                return HAIRCUT_EQUITIES;
            case MBS:
                return HAIRCUT_MBS;
            default:
                return 0.0;
        }
    }

}
