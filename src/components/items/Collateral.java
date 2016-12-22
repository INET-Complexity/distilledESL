package components.items;

import ESL.inventory.Item;

/**
 * This interface allows a contract or good to be pledged as collateral for a repo contract.
 */
public interface Collateral {
    void pledge() throws Exception;
    void unpledge() throws Exception;
    boolean isEncumbered();
}

class CanBeCollateral implements Collateral {
    {
        encumbered = false;
    }

    public void pledge() throws Exception {
        if (!encumbered) {encumbered=true;}
        else { throw new Exception("Already encumbered!");}
    }

    public void unpledge() throws Exception {
        if (!encumbered) {encumbered=false;}
        else { throw new Exception("Already unencumbered!");}
    }

    public boolean isEncumbered() {
        return encumbered;
    }

    private boolean encumbered;

}

class CannotBeCollateral implements Collateral {
    public void pledge() throws Exception {
        throw new Exception("This item cannot be pledged as collateral");
    }

    public void unpledge() throws Exception {
        throw new Exception("This item cannot be pledged (or unpledged) as collateral.");
    }

    public boolean isEncumbered() {return false;}
}