package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;

public class LeverageConstraint {
    private Bank bank;
    private double leverageTarget;
    private double leverageBuffer;
    private double leverageMin;


    public LeverageConstraint(Bank bank, double leverageTarget, double leverageBuffer, double leverageMin) {
        this.bank = bank;
        this.leverageTarget = leverageTarget;
        this.leverageBuffer = leverageBuffer;
        this.leverageMin = leverageMin;

        assert(leverageBuffer >= leverageMin);
        assert(leverageTarget >= leverageMin);
    }

    public boolean isBelowBuffer() {
        return (getLeverage() < leverageBuffer);
    }

    public boolean isBelowMin() {
        return (getLeverage() < leverageMin);
    }

    public double getLeverage() {
        return (1.0 * bank.getGeneralLedger().getEquityValue() / bank.getGeneralLedger().getAssetValue());
    }

    public double getAmountToDelever() {
        return (bank.getGeneralLedger().getAssetValue() * (1 - (getLeverage() / leverageTarget) ));
    }
}
