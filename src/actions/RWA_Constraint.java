package actions;

import agents.Bank;
import contracts.Asset;
import contracts.Contract;
import contracts.Loan;
import demos.Parameters;

import java.util.HashSet;

public class RWA_Constraint {
    private Bank bank;

    public RWA_Constraint(Bank bank) {
        this.bank = bank;
    }

    public double getRWAratio() {
        return 1.0 * bank.getEquityValue() / getRWA();
    }

    public double getRWA() {
        return bank.getMainLedger().getAllAssets().stream()
                .mapToDouble(contract -> contract.getValue(null) * contract.getRWAweight()).sum();
    }
}