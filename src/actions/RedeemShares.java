package actions;

import contracts.Shares;
import contracts.obligations.RedeemSharesObligation;
import demos.Model;
import demos.Parameters;

public class RedeemShares extends Action {

    private Shares shares;

    public RedeemShares(Shares shares) {
        this.shares = shares;
    }

    @Override
    public void perform() {
        shares.addSharesPendingToRedeem((int) getAmount());
        RedeemSharesObligation obligation = new RedeemSharesObligation(shares, (int) getAmount(),
                Model.getTime() + Parameters.TIMESTEPS_TO_REDEEM_SHARES);
        shares.getAssetParty().sendMessage(shares.getLiabilityParty(), obligation);

        Model.redemptionsRecorder.recordRedemption(shares.getAssetParty(), shares.getLiabilityParty(), (int) getAmount());
    }

    @Override
    public double getMax() {

        return shares.getnShares() - shares.getnSharesPendingToRedeem();
    }

    @Override
    public void print() {
        System.out.println("Redeem Shares action by "+shares.getAssetParty().getName()+" -> number: "
                + String.format( "%.2f", getAmount()) +", issues is "+shares.getLiabilityParty().getName());
    }

    public String getName() {
        return "Redeem Shares from "+shares.getLiabilityParty().getName()+" [max: "+getMax()+"]";
    }

    public Shares getShares() {
        return shares;
    }
}
