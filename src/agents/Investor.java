package agents;

import behaviours.Behaviour;
import behaviours.InvestorBehaviour;

public class Investor extends Agent {
    private InvestorBehaviour behaviour;

    public Investor(String name) {
        super(name);
        this.behaviour = new InvestorBehaviour(this);
    }

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public double getLCR() {
        return getCash();
    }
}
