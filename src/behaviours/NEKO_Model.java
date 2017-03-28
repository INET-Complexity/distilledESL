package behaviours;

import agents.Agent;
import contracts.Contract;
import contracts.Loan;
import demos.Parameters;

public class NEKO_Model {

    public static double getValuation(Loan loan) {

        return loan.getPrincipal() * (1.0 - Parameters.NEKO_C * getProbabilityOfDefault(loan.getLiabilityParty()));
    }

    private static double getNonInterbankAssets(Agent agent) {
        return agent.getMainLedger().getAllAssets().stream()
                .filter(contract -> !(contract instanceof Loan))
                .mapToDouble(contract -> contract.getValue(null))
                .sum();
    }

    private static double getProbabilityOfDefault(Agent agent) {
        double equity = agent.getEquityValue();
        if (equity < 0) return 0.0;

        double nonInterbankAssets = getNonInterbankAssets(agent);

        return (equity > nonInterbankAssets) ? 1.0
                : 1.0 * equity / nonInterbankAssets;
    }
}
