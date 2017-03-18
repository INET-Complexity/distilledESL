package actions;

import contracts.Shares;
import contracts.obligations.RedeemSharesObligation;
import demos.BoEDemo;
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
                BoEDemo.getTime() + Parameters.TIMESTEPS_TO_REDEEM_SHARES);
        shares.getAssetParty().sendMessage(shares.getLiabilityParty(), obligation);
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
