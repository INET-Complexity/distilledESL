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


    }

    @Override
    public double getMax() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void print() {

    }
}
