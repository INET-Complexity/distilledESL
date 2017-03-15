package contracts;

import agents.Agent;
import actions.Action;

import java.util.List;

public abstract class Contract {
    public abstract Agent getAssetParty();
    public abstract Agent getLiabilityParty();
    public abstract double getValue();
    public abstract List<Action> getAvailableActions(Agent me);
}
