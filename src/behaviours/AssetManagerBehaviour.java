package behaviours;

import actions.Action;
import actions.SellAsset;
import agents.AssetManager;
import demos.Parameters;

import java.util.ArrayList;

public class AssetManagerBehaviour extends Behaviour {

    private AssetManager me;

    public AssetManagerBehaviour(AssetManager me) {
        super(me);
        this.me = me;
    }

    @Override
    protected void chooseActions() {
        // 1) Check inbox for matured Redeem requests. If we can't meet them right now, default.
        double maturedRedemptions = me.getMaturedObligations();
        if (maturedRedemptions > 0) {
            if (me.getCash() >= maturedRedemptions) {
                me.fulfilMaturedRequests();
            } else {
                //Todo: emergency procedure?
                //triggerDefault();
            }
        }

        // 2) Check inbox for other Redeem requests, find out how much liquidity is needed,
        // and pay all of them now if possible.
        double totalRedemptions = me.getPendingObligations();
        if (totalRedemptions > 0) {
            if (me.getCash() >= totalRedemptions) {
                me.fulfilAllRequests();
                totalRedemptions = 0.0;
            }
        }

        // 3) We must raise an amount of liquidity equal to totalRedemptions plus a fraction.
        double liquidityToRaise = totalRedemptions * (1.0 + Parameters.AM_EXTRA_LIQUIDITY_FRACTION_WHEN_REDEMPTION);


        // We raise the liquidity by selling assets proportionally to initial holdings.

        if (liquidityToRaise > 0) {

            ArrayList<Action> sellAssetActions = getAllActionsOfType(SellAsset.class);
            double totalSellableAssets = sellAssetActions.stream()
                    .mapToDouble(Action::getMax)
                    .sum();

            if (totalSellableAssets < liquidityToRaise) {
                System.out.println("We cannot raise enough liquidity. We will raise as much as possible.");
                liquidityToRaise = totalSellableAssets;
            }

            for (Action action : sellAssetActions) {
                if (action.getMax()>0) {
                    action.setAmount(liquidityToRaise * action.getMax() / totalSellableAssets);
                    addAction(action);
                }
            }
        }

        me.revalueAllExistingShares();
    }
}
