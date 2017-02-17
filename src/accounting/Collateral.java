package accounting;

/**
 * This interface allows the contents of an account to be placed as collateral.
 */
public interface Collateral {
    void pledge() throws Exception;
    void pledge(double amount) throws Exception;
    void unpledge() throws Exception;
    void unpledge(double amount) throws Exception;
    boolean isEncumbered();
    double getEncumberedAmount();
}

class CanBeCollateral implements Collateral {

    CanBeCollateral(double max) {
        encumberedAmount=0.0;
        this.max = max;
    }

    public void pledge() throws Exception {
        if (encumberedAmount<max) {encumberedAmount=max;}
        else { throw new Exception("Already encumbered.");}
    }

    @Override
    public void pledge(double amount) throws Exception {
        if ((max - encumberedAmount)>= amount) {
            encumberedAmount += amount;
        } else {
            throw new Exception("Not enough unencumbered amount.");
        }
    }

    public void unpledge() throws Exception {
        if (encumberedAmount>0) {encumberedAmount=0;}
        else { throw new Exception("Already unencumbered.");}
    }

    @Override
    public void unpledge(double amount) throws Exception {
        if (encumberedAmount >= amount) {
            encumberedAmount -= amount;
        } else {
            throw new Exception("Not enough unencumbered amount.");
        }
    }

    public boolean isEncumbered() {
        return (encumberedAmount>0);
    }

    public double getEncumberedAmount() {
        return encumberedAmount;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    private double encumberedAmount;
    private double max;

}

class CannotBeCollateral implements Collateral {
    public void pledge() throws Exception {
        error();
    }

    public void pledge(double amount) throws Exception {
        error();
    }

    public void unpledge() throws Exception {
        error();
    }

    public void unpledge(double amount) throws Exception {
        error();
    }

    private void error() throws Exception {
        throw new Exception("Error: This item cannot be pledged (or unpledged) as collateral.");
    }

    public boolean isEncumbered() {
        return false;
    }

    public double getEncumberedAmount() {
        return 0;
    }
}
