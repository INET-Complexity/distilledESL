package components.constraints;

public interface HasLeverageConstraint {
    double getAssetValue();
    double getLiabilityValue();
    double getLeverageTarget();
    double getMinimumLeverage();
}
