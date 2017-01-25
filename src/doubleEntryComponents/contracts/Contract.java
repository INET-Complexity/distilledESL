package doubleEntryComponents.contracts;

import doubleEntryComponents.Agent;
import doubleEntryComponents.actions.Action;

import java.util.ArrayList;

public abstract class Contract {
    public abstract Agent getAssetParty();
    public abstract Agent getLiabilityParty();
    public abstract double getValue();
    public abstract ArrayList<Action> getAvailableActions(Agent me);
}
