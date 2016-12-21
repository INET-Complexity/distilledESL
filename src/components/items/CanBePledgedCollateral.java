package components.items;

import ESL.inventory.Item;

/**
 * This interface allows a contract or good to be pledged as collateral for a repo contract.
 */
public interface CanBePledgedCollateral {
    void setEncumbered();
    void setUnencumbered();
    boolean isEncumbered();
}
