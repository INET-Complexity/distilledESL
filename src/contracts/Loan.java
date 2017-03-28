package contracts;

import agents.Agent;
import actions.Action;
import actions.PullFunding;
import actions.PayLoan;
import behaviours.NEKO_Model;
import demos.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Loan extends Contract {
    protected Agent assetParty;
    protected Agent liabilityParty;
    protected double principal;
    private double fundingAlreadyPulled = 0;
    private double previousNekoValuation; //Todo very messy!

    public Loan(Agent assetParty, Agent liabilityParty, double principal) {
        this.assetParty = assetParty;
        this.liabilityParty = liabilityParty;
        this.principal = principal;
        this.previousNekoValuation = getValue(assetParty);
    }

    @Override
    public double getLCRweight() {
        return Parameters.INTERBANK_LCR;
    }

    @Override
    public String getName(Agent me) {
        if (me==assetParty) return "Loan to "+liabilityParty.getName();
        else return "Loan from "+assetParty.getName();
    }

    public void payLoan(double amount) {
        if (liabilityParty != null) liabilityParty.payLiability(amount, this);
        if (assetParty != null) assetParty.pullFunding(amount, this);
        reducePrincipal(amount);
    }

    private void reducePrincipal(double amount) {
        assert (amount <= principal);
        principal -= amount;

        if (principal < 0.01) {
            System.out.println("This loan has been fully repaid.");
            //Todo: and now what shall we do? Destroy the loan?
        }
    }

    public void reducePullFundingAmount(double amount) {
        fundingAlreadyPulled -= amount;
    }

    @Override
    public List<Action> getAvailableActions(Agent me) {
        if (!(principal > 0) || !(principal > fundingAlreadyPulled)) return Collections.emptyList();
//        if (!(assetParty.isAlive() || !(liabilityParty.isAlive()))) return Collections.emptyList();

        ArrayList<Action> availableActions = new ArrayList<>();
        if (assetParty == me) {
            availableActions.add(new PullFunding(me, this));
        } else if (liabilityParty == me) {
            availableActions.add(new PayLoan(me, this));
        }
        return availableActions;
    }

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    @Override
    public double getValue(Agent me) {
//        if (me==assetParty && Parameters.NEKO_MODEL) {
//            return NEKO_Model.getValuation(this);
//        } else {
      return principal;

    }

    public void liquidate() {
        assetParty.devalueAsset(this, principal);
        assetParty.addCash(principal * (1.0 - Parameters.INTERBANK_LOSS_GIVEN_DEFAULT));
        principal = 0.0;
    }

    public void increaseFundingPulled(double fundingPulled) {
        fundingAlreadyPulled += fundingPulled;
    }

    public double getFundingAlreadyPulled() {
        return fundingAlreadyPulled;
    }

    @Override
    public double getRWAweight() {
        return Parameters.INTERBANK_RWAWEIGHT;
    }


    public double getPrincipal() {
        return principal;
    }

    public void reValueLoan() {

        double newValue = Parameters.NEKO_MODEL ?
                NEKO_Model.getValuation(this) : principal;

        if (newValue < previousNekoValuation) {
            assetParty.devalueAsset(this, previousNekoValuation - newValue);
            previousNekoValuation = newValue;
        }
    }
}


