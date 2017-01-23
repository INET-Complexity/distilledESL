package doubleEntryComponents.contracts;

import doubleEntryComponents.Agent;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.actions.SellBond;

import java.util.ArrayList;

public class Bond extends Contract {

    public Bond(Agent assetParty, Agent liabilityParty, MaturityType maturityType, double principal, double rate) {
        this.assetParty = assetParty;
        this.liabilityParty = liabilityParty;
        this.maturityType = maturityType;
        this.principal = principal;
        this.rate = rate;
    }

    public void start() {
        assetParty.add(this);
        liabilityParty.add(this);
    }

    /**
     * Available actions for a bond include:
     * if this bond is encumbered, or if agent is not a party, none.
     * if this is a Gvt bond, an interbank bond or a non-bank bond, the agent gets a SellBond action with
     * the correct parameters
     *
     * @param agent the Agent who is querying its available actions
     * @return an ArrayList of all possible actions for the agent involving this bond
     */
    public ArrayList<Action> getAvailableActions(Agent agent) {
        ArrayList<Action> availableActions = new ArrayList<>();

        if (agent == assetParty) {
            availableActions.add(new SellBond());
            // TODO construct this action correctly?
        } else if (agent == liabilityParty) {
            // If the bond is my liability, I cannot do anything (?)
        }

        return availableActions;
    }

    @Override
    public double getValue() {
        return principal;
    }

    // Setters and Getters
    public Agent getAssetParty() {
        return assetParty;
    }

    public void setAssetParty(Agent assetParty) {
        this.assetParty = assetParty;
    }

    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    public void setLiabilityParty(Agent liabilityParty) {
        this.liabilityParty = liabilityParty;
    }

    public MaturityType getMaturityType() {
        return maturityType;
    }

    public void setMaturityType(MaturityType maturityType) {
        this.maturityType = maturityType;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    private Agent assetParty;
    private Agent liabilityParty;
    private MaturityType maturityType;
    private double principal;
    private double rate;
}