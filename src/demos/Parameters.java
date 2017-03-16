package demos;

import contracts.Asset;
import contracts.Contract;
import contracts.Deposit;

public class Parameters {

    // Contagion Channels
    public static boolean HAIRCUT_CONTAGION = true;
    public static boolean FUNDING_CONTAGION_HEDGEFUND = true;
    public static boolean FUNDING_CONTAGION_INTERBANK = true;
    public static boolean INVESTOR_REDEMPTION = true;
    public static boolean FIRESALE_CONTAGION = true;
    public static boolean CASH_PROVIDER_RUNS = true;

    // sell encumbered assets upon default
    // INTERBANK liquidity hoarding threshold
    // margin calls off
    //
    // NEKO Channel: re-evaluation of interbank assets

    public static double INTERBANK_LOSS_GIVEN_DEFAULT = 0.4; // if Interbank contagion is off



    public static double INITIAL_SHOCK = 0.05;
    public static Asset.AssetType ASSET_TO_SHOCK = Asset.AssetType.EXTERNAL1;

    // Cash Provider
    public static double HAIRCUT_SLOPE = 0.1;
    public static double LEVERAGE_THRESHOLD_TO_RUN = 0.01;
    public static double LCR_THRESHOLD_TO_RUN = 0.01;
    //TODO: REMANE
    public static double CP_FRACTION_TO_RUN = 0.3;

    // Hedgefund parameters
    public static double HF_CASH_BUFFER_AS_FRACTION_OF_ASSETS = 0.05;
    public static double HF_CASH_TARGET_AS_FRACTION_OF_ASSETS = 0.10;


    // Depositor Run
    public static double DEP_LEVERAGE_THRESHOLD_TO_RUN = 0.2;
    public static double DEP_LCR_THRESHOLD_TO_RUN = 0.2;
    public static double DEP_FRACTION_TO_RUN = 0.3;

    // Interbank loans (liquidity hoarding)
    public static double INTERBANK_LEVERAGE_THRESHOLD_TO_RUN = 0.2;
    public static double INTERBANK_LCR_THRESHOLD_TO_RUN = 0.2;
    public static double INTERBANK_FRACTION_TO_RUN = 0.3;

    // Price impacts
    public static double PRICE_IMPACT_MBS = 0.001;
    public static double PRICE_IMPACT_CORPORATE_BONDS = 0.001;
    public static double PRICE_IMPACT_EQUITIES = 0.001;

    // Asset manager
    public static double AM_EXTRA_LIQUIDITY_FRACTION_WHEN_REDEMPTION = 0.05;

    // Investor
    public static double REDEMPTIONS_C1 = 20;
    public static double REDEMPTIONS_C2 = 6;

    public static int TIMESTEPS_TO_PAY = 3;

    public static int SIMULATION_TIMESTEPS = 4;

    // Risk-weighted Assets - weights
    public static double EXTERNAL_ASSETS_WEIGHT = 0.35;


    // Helper functions
    public static double getInitialHaircut(Asset.AssetType assetType) {
        switch (assetType) {
            case CORPORATE_BONDS:
                return 0.1;
            case EQUITIES:
                return 0.1;
            case MBS:
                return 0.1;
            default:
                return 0.0;
        }
    }

    public static double getRWAWeight(Asset.AssetType assetType) {
        switch (assetType) {
            case CORPORATE_BONDS:
                return 1.0;
            case EQUITIES:
                return 0.75;
            case MBS:
                return 0.35;
            default:
                return 0.0;
        }
    }

    public static double DEPOSITS_LCR = 0.1;
    public static double REPO_LCR = 0.5;
    public static double INTERBANK_LCR = 1.0;
    public static double LONG_TERM_LCR = 0.05;
    public static double MATCH_BOOK_LCR = 0.0;

    //todo: put this in for the LCR!


    public static double INTERBANK_RWAWEIGHT = 0.40;
    //todo: compute RWA

    public static boolean ASSET_MANAGER_ON = INVESTOR_REDEMPTION;
    public static boolean HEDGEFUNDS_ON = true;

    public static double HF_DEFAULT_LEVERAGE_TARGET = 0.02;
    public static double HF_DEFAULT_LEVERAGE_BUFFER = 0.01;
    public static double HF_DEFAULT_LEVERAGE_MIN = 0.0;

}
