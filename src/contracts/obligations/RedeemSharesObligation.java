package contracts.obligations;

import contracts.Shares;

public class RedeemSharesObligation extends Obligation {


    private Shares shares;
    private final int nSharesToRedeem;

    public RedeemSharesObligation(Shares shares, int numberOfShares, int timeToPay) {
        super(shares, numberOfShares * shares.getNAV(), timeToPay);
        this.shares = shares;
        this.nSharesToRedeem = numberOfShares;
    }

    @Override
    public double getAmount() {
        return (nSharesToRedeem * shares.getNAV());
    }

    @Override
    public void fulfil() {
        shares.redeem(nSharesToRedeem);
        System.out.println(shares.getLiabilityParty().getName()+" has fulfilled an obligation to redeem shares and pay " +
                shares.getAssetParty().getName()+
                " an amount "+String.format("%.2f", amount)+".");
        setFulfilled();
    }
}
