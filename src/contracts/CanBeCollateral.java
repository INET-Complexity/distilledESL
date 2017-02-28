package contracts;

public interface CanBeCollateral {
    void encumber(double quantity);
    void unEncumber(double quantity);
    double getPrice();
    double getHairCut();
    double getMaxEncumberableQuantity();
    double getMaxEncumberableValue();
}
