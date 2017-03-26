package agents;

import behaviours.Behaviour;
import behaviours.CashProviderBehaviour;

public class CashProvider extends Agent {

    private CashProviderBehaviour behaviour;

    public CashProvider(String name) {

        super(name);
        this.behaviour = new CashProviderBehaviour(this);
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
