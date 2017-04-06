package actions;

import agents.Bank;
import contracts.ContractStress;

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
                .mapToDouble(contract -> contract.getValue(null) * ((ContractStress)contract).getRWAweight()).sum();
    }
}