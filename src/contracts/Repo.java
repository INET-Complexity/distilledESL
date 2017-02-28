package contracts;

import actions.Action;
import actions.MarginCall;
import agents.Agent;
import agents.CanPledgeCollateral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A Repo is a securitized Loan, i.e. it includes collateral. Collateral is stored as hashmap of CanBeCollateral contracts
 * and quantities of each. The Repo can value its collateral by valuing each contract in the Collateral hashmap, and
 * weighting each by its haircut.
 *
 *
 * @author rafa
 */
public class Repo extends Loan {

    public Repo(Agent assetParty, Agent liabilityParty, double principal) {
        super(assetParty, liabilityParty, principal);
        this.collateral = new HashMap<>();
    }

    public void pledgeCollateral(CanBeCollateral asset, double quantity) {
        asset.encumber(quantity);

        if (collateral.containsKey(asset)) {
            collateral.put(asset, quantity + collateral.get(asset));
        } else {
            collateral.put(asset, quantity);
        }
    }

    public void unpledgeCollateral(CanBeCollateral asset, double quantity) {
        asset.unEncumber(quantity);

        assert(collateral.containsKey(asset));
        collateral.put(asset, collateral.get(asset) - quantity);
    }

    public void marginCall() {
        double currentValue = valueCollateral();
        if (currentValue < principal) { //TODO: include finite precision? i.e. currentValue < principal + smallNumber
            // Need to put more collateral!
            assert(liabilityParty instanceof CanPledgeCollateral);
            ((CanPledgeCollateral) liabilityParty).putMoreCollateral(principal - currentValue, this);

        } else if (currentValue > principal) {
            // What?
        }
    }

    private double valueCollateral() {
        double value = 0;

        for (Map.Entry<CanBeCollateral, Double> entry : collateral.entrySet()) {
            CanBeCollateral asset = entry.getKey();
            Double quantity = entry.getValue();

            value += asset.getPrice() * quantity * asset.getHairCut();
        }

        return value;
    }

    private HashMap<CanBeCollateral, Double> collateral;

    @Override
    public Agent getAssetParty() {
        return assetParty;
    }

    @Override
    public Agent getLiabilityParty() {
        return liabilityParty;
    }

    @Override
    public double getValue() {
        return principal;
    }

    @Override
    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = super.getAvailableActions(me);
        if (me == assetParty) {
            availableActions.add(new MarginCall(this));
        }
        return availableActions;
    }
}
