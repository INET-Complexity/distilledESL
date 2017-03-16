package demos;

import contracts.Asset;

public class Parameters {

    // Contagion Channels
    public static boolean HAIRCUT_CONTAGION = true;
    public static boolean FUNDING_CONTAGION = true;
    public static boolean FIRESALE_CONTAGION = true;
    public static boolean INVESTOR_REDEMPTION = true;

    // Haircuts
    public static double HAIRCUT_MBS = 0.1;
    public static double HAIRCUT_CORPORATE_BONDS = 0.1;
    public static double HAIRCUT_EQUITIES = 0.1;

    // Cash Provider
    public static double HAIRCUT_SLOPE = 0.1;

    // Price impacts
    public static double PRICE_IMPACT_MBS = 0.001;
    public static double PRICE_IMPACT_CORPORATE_BONDS = 0.001;
    public static double PRICE_IMPACT_EQUITIES = 0.001;

    // Asset manager
    public static double ASSET_MANAGER_LIQUIDITY_FRACTION = 0.05;

    // Investor
    public static double REDEMPTIONS_C1 = 20;
    public static double REDEMPTIONS_C2 = 6;


    // Helper functions
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
