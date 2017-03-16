package contracts;

import actions.Action;
import actions.RedeemShares;
import agents.Agent;
import agents.CanIssueShares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This contract represents a bunch of shares of some Agent which can issue shares.
 */
public class Shares extends Contract {
    private Agent owner;
    private CanIssueShares issuer;
    private int numberOfShares;

    public Shares(Agent owner, CanIssueShares issuer, int numberOfShares) {
        this.owner = owner;
        this.issuer = issuer;
        this.numberOfShares = numberOfShares;

        assert(issuer instanceof Agent);
    }

    public void redeem(int numberToRedeem) {
        assert(numberToRedeem <= numberOfShares);
        numberOfShares -= numberToRedeem;
        if (numberOfShares == 0) {//todo: finite precision
            // todo: destroy shares?
        }
    }

    @Override
    public Agent getAssetParty() {
        return owner;
    }

    @Override
    public Agent getLiabilityParty() {
        return (Agent) issuer;
    }

    public double getValue() {
        return numberOfShares * issuer.getNetAssetValue();
    }

    public int getNumberOfShares() {return numberOfShares;}

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(me==owner) || !(numberOfShares > 0)) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        availableActions.add(new RedeemShares(this));
        return availableActions;
    }
}


