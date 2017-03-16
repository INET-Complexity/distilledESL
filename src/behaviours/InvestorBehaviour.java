package behaviours;

import actions.Action;
import actions.RedeemShares;
import agents.CanIssueShares;
import agents.Investor;
import contracts.Shares;
import demos.Parameters;

import java.util.ArrayList;
import java.util.HashMap;

public class InvestorBehaviour extends Behaviour {
    private Investor me;
    private HashMap<CanIssueShares, Double> previousNAVs;

    public InvestorBehaviour(Investor me) {
        super(me);
        this.me = me;
        this.previousNAVs = new HashMap<>();
    }

    @Override
    protected void chooseActions() {
        // Investors only act if INVESTOR_REDEMPTION is enabled
        if ( ! Parameters.INVESTOR_REDEMPTION) return;

        // Get all Shares owned
        ArrayList<Action> redeemActions = getAllActionsOfType(RedeemShares.class);

        for (Action action : redeemActions) {
            RedeemShares redeemAction = (RedeemShares) action;
            Shares shares =  redeemAction.getShares();
            CanIssueShares firm = (CanIssueShares) shares.getLiabilityParty();

            // We only redeem shares if we have a value for the 'previous NAV'.
            if (previousNAVs.containsKey(firm)) {
                double previousNAV = previousNAVs.get(firm);
                double currentNAV = firm.getNetAssetValue();

                // We use the previous NAV and the current NAV to compute the fraction of shares to redeem
                double fractionToRedeem = Parameters.REDEMPTIONS_C1 * (
                        Math.exp(Parameters.REDEMPTIONS_C2
                                * Math.min(-100.0 * (currentNAV-previousNAV) / currentNAV, 0.0)) - 1)
                        / (Math.E - 1);

                // Work out how many shares I must redeem given that fraction
                int sharesToRedeem = (int) Math.floor(fractionToRedeem * shares.getNumberOfShares());

                redeemAction.setAmount(sharesToRedeem);
                addAction(redeemAction);
            }

        }

    }
}
