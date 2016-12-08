package components;

import ESL.agent.Agent;
import ESL.contract.obligation.Obligation;

public class PaymentObligation extends Obligation {

    private Double amount;

    PaymentObligation(Agent to, Agent from, Double amount) {
        super(to, from, null);
        this.amount=amount;
    }

    public Double getAmount() {
        return amount;
    }
}
