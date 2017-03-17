package contracts;

import actions.Action;
import actions.RedeemShares;
import agents.Agent;
import agents.CanIssueShares;
import contracts.obligations.Obligation;
import contracts.obligations.RedeemSharesObligation;
import demos.Parameters;

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
    private double previousValueOfShares;
    private double originalNAV;

    public Shares(Agent owner, CanIssueShares issuer, int numberOfShares, double originalNAV) {
        this.owner = owner;
        this.issuer = issuer;
        this.numberOfShares = numberOfShares;
        this.previousValueOfShares = getValue();
        this.originalNAV = originalNAV;

        assert(issuer instanceof Agent);
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
        assert(numberToRedeem <= numberOfShares);
        Obligation paymentObligation =
                new RedeemSharesObligation(this, numberToRedeem * issuer.getNetAssetValue(),
                        Parameters.TIMESTEPS_TO_PAY);

        ((Agent) issuer).addToInbox(paymentObligation);
        owner.addToOutbox(paymentObligation);
    }

    public void cashIn(double amount) {
        ((Agent) issuer).payLiability(amount, this);
        owner.sellAssetForValue(this, amount);
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

    public void updateValue() {
        double valueChange = getValue() - previousValueOfShares;
        previousValueOfShares = getValue();

        if (valueChange > 0) {
            owner.appreciateAsset(this, valueChange);
            ((Agent) issuer).appreciateLiability(this, valueChange);
        } else if (valueChange < 0) {
            owner.devalueAsset(this, -1.0 * valueChange);
            ((Agent) issuer).devalueLiability(this, -1.0 * valueChange);
        }
    }

    public double getOriginalNAV() {
        return originalNAV;
    }
}


