package contracts.obligations;

import contracts.Shares;

public class RedeemSharesObligation extends Obligation {


    private Shares shares;
    private final int nSharesToRedeem;

    public RedeemSharesObligation(Shares shares, int numberOfShares, int timeLeftToPay) {
        super(shares, numberOfShares * shares.getNAV(), timeLeftToPay);
        this.shares = shares;
        this.nSharesToRedeem = numberOfShares;
    }

    @Override
    void tick() {
        super.tick();
        setAmount(nSharesToRedeem * shares.getNAV());
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
