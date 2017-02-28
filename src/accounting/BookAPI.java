package accounting;

import contracts.Contract;

/**
 * Interface for a Book (operations that must be provided at the very least by a Book implementation).
 *
 * @author rafa
 */
public interface BookAPI {

    double getAssetValue();
    double getLiabilityValue();
    double getEquityValue();
    double getAssetValueOf(Class<? extends Contract> contractType);
    double getLiabilityValueOf(Class<? extends Contract> contractType);

    void addAsset(Contract contract);
    void addLiability(Contract contract);
    void addCash(double amount);


    void sellAsset(double amount, Class<? extends Contract> assetType);
    void payLiability(double amount, Class<? extends Contract> liabilityType);


}
