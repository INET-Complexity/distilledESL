package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;

import static java.lang.Math.max;

/**
 * An LCR constraint. LCR = liquid assets / (cash outflows - inflows)
 */
public class LCR_Constraint {

    private Bank bank;
    private double LCR_target;
    private double LCR_buffer;
    private double LCR_min;
    private final double LCR_denominator;


    /**
     * Simplified constructor where the denominator of the LCR equation is not calculated, but considered fixed.
     *
     * @param bank the bank the constraint applies to
     * @param LCR_target desired level
     * @param LCR_buffer level at which to act
     * @param LCR_min absolute minimum; below minimum usually means default
     * @param LCR_denominator this is given as an input in this simulation, since cash inflows/outflows are not being modelled.
     */
    public LCR_Constraint(Bank bank, double LCR_target, double LCR_buffer, double LCR_min, double LCR_denominator) {
        this.bank = bank;
        this.LCR_target = LCR_target;
        this.LCR_buffer = LCR_buffer;
        this.LCR_min = LCR_min;
        this.LCR_denominator = LCR_denominator;

        assert((LCR_target >= LCR_buffer) && (LCR_buffer >= LCR_min));
    }

    public boolean isBelowBuffer() {
        return (getLCR() < LCR_buffer);
    }

    public boolean isBelowMin() {
        return (getLCR() < LCR_min);
    }

    public double getLCR() {
        return (1.0 * bank.getCash() / LCR_denominator);
    }

    public double getLiquidityToRaise() {
        return max(LCR_denominator * (LCR_target - getLCR()), 0.0);
    }
}