package doubleEntryComponents.contracts;

import doubleEntryComponents.Agent;

public abstract class Contract {
    public abstract Agent getAssetParty();
    public abstract Agent getLiabilityParty();
    public abstract double getValue();

}
