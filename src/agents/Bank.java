package agents;

import actions.Action;
import actions.LCR_Constraint;
import actions.BankLeverageConstraint;
import actions.SellAsset;
import behaviours.BankBehaviour;
import contracts.*;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class represents a simple me with a single Ledger, called 'general Ledger'.
 *
 * Every Bank has a BankBehaviour.
 */
public class Bank extends Agent implements CanPledgeCollateral {

    private BankLeverageConstraint bankLeverageConstraint;
    private LCR_Constraint lcr_constraint;

    public Bank(String name) {
        super(name);
        this.bankLeverageConstraint = new BankLeverageConstraint(this);
        this.lcr_constraint = new LCR_Constraint(this, 1.0, 1.0, 1.0, 20.0);
        this.behaviour = new BankBehaviour(this);
    }

    //Todo: is this the best way to do this? This should really be in Behaviour
    public void raiseLiquidity(double liquidityNeeded) {
        ArrayList<Action> availableActions = getAvailableActions(this);

        double initialAssetHoldings = mainLedger.getAssetValueOf(Asset.class);

        for (Action action : availableActions) {
            if (action instanceof SellAsset) {
                action.setAmount(action.getMax()*liquidityNeeded/initialAssetHoldings);
                action.print();
                action.perform();
            }
        }

    }

    @Override
    public void putMoreCollateral(double total, Repo repo) {
        // First, get a set of all my Assets that can be pledged as collateral
        HashSet<Contract> potentialCollateral = mainLedger.getAssetsOfType(AssetCollateral.class);

        double maxHaircutValue = 0.0;
        for (Contract contract : potentialCollateral) {
            assert(contract instanceof CanBeCollateral);
            CanBeCollateral asset = (CanBeCollateral) contract;
            maxHaircutValue += asset.getMaxEncumberableValue() * (1.0 - asset.getHairCut());
        }

        for (Contract contract : potentialCollateral) {
            CanBeCollateral asset = (CanBeCollateral) contract;

            double quantityToPledge = total * asset.getMaxEncumberableValue() * (1.0 - asset.getHairCut()) / maxHaircutValue;
            repo.pledgeCollateral(asset, quantityToPledge);

        }
    }


    public void withdrawCollateral(double excessValue, Repo repo) {
        repo.unpledgeProportionally(excessValue);
    }


    public void setBankLeverageConstraint(BankLeverageConstraint bankLeverageConstraint) {
        this.bankLeverageConstraint = bankLeverageConstraint;
    }

    public BankLeverageConstraint getBankLeverageConstraint() {
        return bankLeverageConstraint;
    }

    public LCR_Constraint getLCR_constraint() {
        return lcr_constraint;
    }

    public void setLCR_constraint(LCR_Constraint lcr_constraint) {
        this.lcr_constraint = lcr_constraint;
    }
}
