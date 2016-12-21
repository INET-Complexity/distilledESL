package components.constraints;

public interface HasRWLeverageConstraint {

    double getRWAssetValue();
    double getRWLiabilityValue();
    double getRWLeverageTarget();
    double getRWMinimumLeverage();

}
