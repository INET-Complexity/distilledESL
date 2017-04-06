package contracts;

import agents.StressAgent;
import actions.Action;
import economicsl.Agent;

import java.util.ArrayList;
import java.util.List;

public class Bond extends ContractStress {

    public Bond(StressAgent assetParty, StressAgent liabilityParty, MaturityType maturityType, double principal, double rate) {
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

    @Override
    public String getName(Agent me) {
        return "Bond. NOT IMPLEMENTED WHY ARE YOU USING ME???";
    }

    /**
     * Available actions for a bond include:
     * if this bond is encumbered, or if agent is not a party, none.
     * if this is a Gvt bond, an interbank bond or a non-me bond, the agent gets a SellBond action with
     * the correct parameters
     *
     * @param agent the StressAgent who is querying its available actions
     * @return an ArrayList of all possible actions for the agent involving this bond
     */
    public List<Action> getAvailableActions(Agent agent) {
        ArrayList<Action> availableActions = new ArrayList<>();

        if (agent == assetParty) {
            //availableActions.add(new SellBond());
            // TODO construct this action correctly?
        } else if (agent == liabilityParty) {
            // If the bond is my liability, I cannot do anything (?)
        }

        return availableActions;
    }

    @Override
    public double getValue(Agent me) {
        return principal;
    }


    // Setters and Getters
    public StressAgent getAssetParty() {
        return assetParty;
    }

    public void setAssetParty(StressAgent assetParty) {
        this.assetParty = assetParty;
    }

    public StressAgent getLiabilityParty() {
        return liabilityParty;
    }

    public void setLiabilityParty(StressAgent liabilityParty) {
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

    private StressAgent assetParty;
    private StressAgent liabilityParty;
    private MaturityType maturityType;
    private double principal;
    private double rate;

}