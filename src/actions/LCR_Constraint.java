package actions;

import agents.Bank;
import contracts.ContractStress;
import demos.Parameters;

import static java.lang.Math.max;

/**
 * An LCR constraint. LCR = liquid assets / (cash outflows - inflows)
 */
public class LCR_Constraint {

    private Bank bank;

    /**
     * Simplified constructor where the denominator of the LCR equation is not calculated, but considered fixed.
     *
     * @param bank the me the constraint applies to
     * */
    public LCR_Constraint(Bank bank) {
        this.bank = bank;
    }

    public boolean isBelowBuffer() {
        return (getLCR() < Parameters.BANK_LCR_BUFFER);
    }

    public boolean isBelowMin() {
        return (getLCR() < Parameters.BANK_LCR_MIN);
    }

    private double getLCRdenominator() {
        return bank.getMainLedger().getAllLiabilities().stream()
                .mapToDouble(contract -> contract.getValue(null) * ((ContractStress)contract).getLCRweight()).sum();
    }
    public double getLCR() {
        return 1.0 * bank.getCash_() - bank.getEncumberedCash() / getLCRdenominator();
    }

    public double getLiquidityToRaise() {
        return max(getLCRdenominator() * (Parameters.BANK_LCR_TARGET - getLCR()), 0.0);
    }

    public double getCashTarget() {
        return Parameters.BANK_LCR_TARGET * getLCRdenominator();
    }

    public double getCashBuffer() {
        return Parameters.BANK_LCR_BUFFER * getLCRdenominator();
    }
}