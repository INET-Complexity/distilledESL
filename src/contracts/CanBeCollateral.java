package contracts;

import agents.Agent;

public interface CanBeCollateral {
    void encumber(double quantity);
    void unEncumber(double quantity);
    double getPrice();
    double getHaircut();
    double getUnencumberedQuantity();
    double getUnencumberedValue();
    AssetCollateral changeOwnership(Agent newOwner, double amount);
}
