package org.economicsl.accounting;

import org.economicsl.Contract;

/**
 * Interface for a Ledger (operations that must be provided at the very least by a Ledger implementation).
 *
 * @author rafa
 */
public interface LedgerAPI {

    double getAssetValue();
    double getLiabilityValue();
    double getEquityValue();
    double getAssetValueOf(Class<?> contractType);
    double getLiabilityValueOf(Class<?> contractType);

    void addAsset(Contract contract);
    void addLiability(Contract contract);
    void addCash(double amount);


    void sellAsset(double amount, Class<? extends Contract> assetType);
    void payLiability(double amount, Contract liability);



}
