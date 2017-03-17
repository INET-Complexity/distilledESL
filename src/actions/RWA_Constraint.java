package actions;

import agents.Bank;
import contracts.Asset;
import contracts.Contract;
import demos.Parameters;

public class RWA_Constraint {
    private Bank bank;

    public RWA_Constraint(Bank bank) {
        this.bank = bank;
    }

    public double getRWAratio() {
        return 1.0 * bank.getEquityValue() / getRWA();
    }
    public double getRWA() {
        return bank.getMainLedger().getAssetsOfType(Asset.class).stream()
                .mapToDouble(asset -> asset.getValue() * Parameters.getRWAWeight(((Asset) asset).getAssetType()))
                .sum();
    }
}