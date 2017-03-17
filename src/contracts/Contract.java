package contracts;

import agents.Agent;
import actions.Action;

import java.util.List;

public abstract class Contract {
    public abstract Agent getAssetParty();
    public abstract Agent getLiabilityParty();
    public abstract double getValue(Agent me);
    public double getValue() {return getValue(null);}
    public abstract List<Action> getAvailableActions(Agent me);
    public abstract String getName(Agent me);
    public double getLCRweight() {return 0.0;}
}

