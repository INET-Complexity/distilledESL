package contracts;

import agents.Agent;
import actions.Action;

import java.util.ArrayList;

public abstract class Contract {
    public abstract Agent getAssetParty();
    public abstract Agent getLiabilityParty();
    public abstract double getValue();
    public abstract ArrayList<Action> getAvailableActions(Agent me);
}
