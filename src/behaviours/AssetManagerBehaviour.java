package behaviours;

import agents.AssetManager;
import demos.Parameters;

public class AssetManagerBehaviour extends Behaviour {

    private AssetManager me;

    public AssetManagerBehaviour(AssetManager me) {
        super(me);
        this.me = me;
    }

    @Override
    protected void chooseActions() throws DefaultException {
        // Plot net asset value?
        System.out.println("\nMy NAV is "+me.getNetAssetValue());
        // 1) Pay matured cash commitments or default.
        double maturedPullFunding = me.getMaturedObligations();
        if (maturedPullFunding > 0) {
            System.out.println("We have matured payment obligations for a total of " + String.format("%.2f", maturedPullFunding));
            if (me.getCash() >= maturedPullFunding) {
                me.fulfilMaturedRequests();
            } else {
                System.out.println("A matured obligation was not fulfilled.");
                throw new DefaultException(me, DefaultException.TypeOfDefault.LIQUIDITY);
            }
        }

        // Sell assets to pay off all the other cash commitments.
        double liquidityNeeded = (1 + Parameters.AM_EXTRA_LIQUIDITY_FRACTION_WHEN_REDEMPTION)
                * me.getAllPendingObligations() - me.getCash();

        // Firesale to raise that liquidity
        if (liquidityNeeded > 0) sellAssetsProportionally(liquidityNeeded);

}

}
