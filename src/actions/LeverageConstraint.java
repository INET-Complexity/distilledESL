package actions;

import agents.Bank;

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

        assert((leverageTarget >= leverageBuffer) && (leverageBuffer >= leverageMin));
    }

    public boolean isBelowBuffer() {
        return (getLeverage() < leverageBuffer);
    }

    public boolean isBelowMin() {
        return (getLeverage() < leverageMin);
    }

    public double getLeverage() {
        return (1.0 * bank.getEquityValue() / bank.getAssetValue());
    }

    public double getAmountToDelever() {
        return (bank.getMainBook().getAssetValue() * (1 - (getLeverage() / leverageTarget) ));
    }
}
