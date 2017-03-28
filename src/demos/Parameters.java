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
    public static boolean NEKO_MODEL = false;
    public static boolean FIRESALES_UPON_DEFAULT = true;

    // Printing out results
    public static boolean PRINT_BALANCE_SHEETS = false;
    public static boolean PRINT_LIQUIDITY = false;
    public static boolean PRINT_MAILBOX = false;




    // Agents on or off
    public static boolean ASSET_MANAGER_ON = true;
    public static boolean HEDGEFUNDS_ON = true;


    // INTERBANK liquidity hoarding threshold
    // margin calls off
    //

    public static double INTERBANK_LOSS_GIVEN_DEFAULT = 0.4; // if Interbank contagion is off



    public static double INITIAL_SHOCK = 0.16;
    public static Asset.AssetType ASSET_TO_SHOCK = Asset.AssetType.EXTERNAL2;

    // Cash Provider
    public static double HAIRCUT_SLOPE = 3.0; //0.2;
    public static double HAIRCUT_PRICE_FALL_THRESHOLD = 0.001; //0.05;

    public static double LCR_THRESHOLD_TO_RUN = -3.0;
    public static double LEVERAGE_THRESHOLD_TO_RUN = 0.0075;
    public static double CP_FRACTION_TO_RUN = 0.3;

    public static int TRIAL_PERIOD = 5;

    // Hedgefund parameters
    public static double HF_CASH_BUFFER_AS_FRACTION_OF_ASSETS = 0.04;
    public static double HF_CASH_TARGET_AS_FRACTION_OF_ASSETS = 0.08;

    public static double HF_LEVERAGE_BUFFER = 0.03;
    public static double HF_LEVERAGE_TARGET = 0.06;


    // Depositor Run
    public static double DEP_LEVERAGE_THRESHOLD_TO_RUN = 0.2;
    public static double DEP_LCR_THRESHOLD_TO_RUN = 0.2;
    public static double DEP_FRACTION_TO_RUN = 0.3;

    // Interbank loans (liquidity hoarding)
    public static double INTERBANK_LEVERAGE_THRESHOLD_TO_RUN = 0.2;
    public static double INTERBANK_LCR_THRESHOLD_TO_RUN = 0.2;
    public static double INTERBANK_FRACTION_TO_RUN = 0.3;

    // Price impacts
    public static double PRICE_IMPACT_MBS = 0.0001;
    public static double PRICE_IMPACT_CORPORATE_BONDS = 0.0001;
    public static double PRICE_IMPACT_EQUITIES = 0.0001;

    // Asset manager
    public static double AM_EXTRA_LIQUIDITY_FRACTION_WHEN_REDEMPTION = 0.05;

    // Investor
    public static double REDEMPTIONS_C1 = 20;
    public static double REDEMPTIONS_C2 = 2;

    public static double REDEMPTIONS_FRACTION = 0.25;

    public static double NEKO_C = 0.01;//0.6;

    public static int TIMESTEPS_TO_PAY = 3;
    public static int TIMESTEPS_TO_REDEEM_SHARES = 2;

    public static int SIMULATION_TIMESTEPS = 15;


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
                return 1.00;
            case EQUITIES:
                return 0.75;
            case MBS:
                return 0.35;
            case EXTERNAL1:
                return 0.35;
            case EXTERNAL2:
                return 0.35;
            case EXTERNAL3:
                return 0.35;
            default:
                return 0.0;
        }
    }

    public static double INTERBANK_RWAWEIGHT = 0.40;


    public static double DEPOSITS_LCR = 0.10;
    public static double REPO_LCR = 0.50;
    public static double INTERBANK_LCR = 1.00;
    public static double LONG_TERM_LCR = 0.05;
    public static double MATCH_BOOK_LCR = 0.00;


    public static double BANK_LCR_MIN = 0.05;
    public static double BANK_LCR_BUFFER = 0.10;
    public static double BANK_LCR_TARGET = 0.15;

    public static double BANK_LEVERAGE_MIN = 0.00;
    public static double BANK_LEVERAGE_BUFFER = 0.015;
    public static double BANK_LEVERAGE_TARGET = 0.02;


    // Recording
    public static boolean RECORD_DEFAULT = true;

}