package contracts;

import agents.StressAgent;

public interface CanBeCollateral {
    void encumber(double quantity);
    void unEncumber(double quantity);
    double getPrice();
    double getHaircut();
    double getUnencumberedQuantity();
    double getUnencumberedValue();
    AssetCollateral changeOwnership(StressAgent newOwner, double amount);
}
