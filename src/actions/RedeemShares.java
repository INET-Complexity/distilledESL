package actions;

import contracts.Shares;

public class RedeemShares extends Action {

    private Shares shares;

    public RedeemShares(Shares shares) {
        // We must send a request to pay to redeem the shares.
        this.shares = shares;
    }

    @Override
    public void perform() {
        shares.redeem((int) getAmount());
    }

    @Override
    public double getMax() {
        return shares.getNumberOfShares();
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
