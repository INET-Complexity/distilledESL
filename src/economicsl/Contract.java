package economicsl;

import actions.Action;

import java.util.List;

/**
 * Created by taghawi on 06/04/17.
 */
public abstract class Contract {
    public abstract Agent getAssetParty();

    public abstract Agent getLiabilityParty();

    public abstract double getValue(Agent me);

    public abstract List<Action> getAvailableActions(Agent me);

    public abstract String getName(Agent me);
}
