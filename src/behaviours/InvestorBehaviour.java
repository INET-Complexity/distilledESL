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

    public InvestorBehaviour(Investor me) {
        super(me);
        this.me = me;
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

            double originalNAV = shares.getOriginalNAV();
            double currentNAV = firm.getNetAssetValue();

            // We use the original NAV and the current NAV to compute the fraction of shares to redeem
//            double fractionToRedeem = Parameters.REDEMPTIONS_C1 * (
//                    Math.exp(Parameters.REDEMPTIONS_C2
//                            * Math.min( (currentNAV-originalNAV) / originalNAV, 0.0)) - 1)
//                    / (Math.E - 1);

            double lossInNAV = (1.0*(originalNAV - currentNAV) / originalNAV);
            System.out.println("Loss in NAV is "+lossInNAV);

            double f = Parameters.REDEMPTIONS_FRACTION;
            double fractionToRedeem = lossInNAV < 0.005 ? 0.0 :
                    lossInNAV < 0.05 ? f :
                            lossInNAV < 0.10 ? 2*f :
                                    lossInNAV< 0.15 ? 3*f : 4*f;

            System.out.println("Fraction to redeem "+fractionToRedeem);

            // Work out how many shares I must redeem given that fraction
            int sharesToRedeem = (int) Math.floor(fractionToRedeem * shares.getOriginalNumberOfShares());
            sharesToRedeem = Math.max(0, shares.getnShares() - shares.getnSharesPendingToRedeem() - (shares.getOriginalNumberOfShares() - sharesToRedeem));

            if (sharesToRedeem > 0) {
                System.out.println("The NAV of "+shares.getLiabilityParty().getName()+
                        " has dropped from its original value of "+originalNAV+" to "+currentNAV +
                " so I am redeeming "+sharesToRedeem+" shares.");


                redeemAction.setAmount(sharesToRedeem);
                redeemAction.perform();
            }

        }

    }
}
