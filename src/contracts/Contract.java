package contracts;

import agents.StressAgent;
import actions.Action;
import agents.Agent;

import java.util.List;

public abstract class Contract {
    public abstract StressAgent getAssetParty();
    public abstract StressAgent getLiabilityParty();
    public abstract double getValue(Agent me);
    public abstract List<Action> getAvailableActions(Agent me);
    public abstract String getName(Agent me);
    public double getLCRweight() {return 0.0;}
    public double getRWAweight() {return 0.0;}
}

