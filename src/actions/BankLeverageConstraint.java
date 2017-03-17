package actions;

import agents.Bank;
import demos.Parameters;

public class BankLeverageConstraint {
    private Bank bank;

    public BankLeverageConstraint(Bank bank) {
        this.bank = bank;
    }

    public boolean isBelowBuffer() {
        return (bank.getLeverage() < Parameters.BANK_LEVERAGE_BUFFER);
    }

    public boolean isBelowMin() {
        return (bank.getLeverage() < Parameters.BANK_LEVERAGE_MIN);
    }

    public double getAmountToDelever() {
        return (bank.getEquityValue() * (1.0 / bank.getLeverage() - 1.0 / Parameters.BANK_LEVERAGE_TARGET));
    }
}
