package contracts;

import actions.Action;
import actions.RedeemShares;
import agents.StressAgent;
import economicsl.Agent;
import agents.CanIssueShares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This contract represents a bunch of shares of some StressAgent which can issue shares.
 */
public class Shares extends Contract {
    private StressAgent owner;
    private CanIssueShares issuer;
    private int nShares;
    private double previousValueOfShares;
    private double originalNAV;
    private int nSharesPendingToRedeem;
    private int originalNumberOfShares;

    public Shares(StressAgent owner, CanIssueShares issuer, int nShares, double originalNAV) {
        this.owner = owner;
        this.issuer = issuer;
        this.nShares = nShares;
        this.originalNumberOfShares = nShares;
        this.previousValueOfShares = getNewValue();
        this.originalNAV = originalNAV;
        this.nSharesPendingToRedeem = 0;

        assert(issuer instanceof StressAgent);
    }

    @Override
    public String getName(Agent me) {
        if (me==owner) {
            return "Shares of the firm: "+((Agent) issuer).getName();
        } else {
            return "Shares owned by our shareholder "+owner.getName();
        }
    }

    public void redeem(int numberToRedeem) {
        assert(numberToRedeem <= nShares);
        double nav = getNAV();
        ((StressAgent) issuer).payLiability(numberToRedeem * nav, this);
        owner.sellAssetForValue(this, numberToRedeem * nav);
        nShares -= numberToRedeem;
        nSharesPendingToRedeem -= numberToRedeem;
    }



    @Override
    public StressAgent getAssetParty() {
        return owner;
    }

    @Override
    public StressAgent getLiabilityParty() {
        return (StressAgent) issuer;
    }

    @Override
    public double getValue(Agent me) {
        return previousValueOfShares;}

    private double getNewValue() {return nShares * issuer.getNetAssetValue();}

    public double getNAV() { return issuer.getNetAssetValue(); }

    public int getnShares() {return nShares;}

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(me==owner) || !(nShares > 0)) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        availableActions.add(new RedeemShares(me, this));
        return availableActions;
    }

    public void updateValue() {
        double valueChange = getNewValue() - previousValueOfShares;
        previousValueOfShares = getNewValue();

        if (valueChange > 0) {
            owner.appreciateAsset(this, valueChange);
            ((StressAgent) issuer).appreciateLiability(this, valueChange);
        } else if (valueChange < 0) {
            System.out.println("value of shares fell.");
            owner.devalueAsset(this, -1.0 * valueChange);
            ((StressAgent) issuer).devalueLiability(this, -1.0 * valueChange);
        }
    }

    public double getOriginalNAV() {
        return originalNAV;
    }

    public void addSharesPendingToRedeem(int number) {
        nSharesPendingToRedeem += number;
    }

    public int getnSharesPendingToRedeem() {
        return nSharesPendingToRedeem;
    }

    public int getOriginalNumberOfShares() {
        return originalNumberOfShares;
    }
}


