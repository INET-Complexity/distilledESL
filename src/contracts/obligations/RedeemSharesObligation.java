package contracts.obligations;

import contracts.Shares;

public class RedeemSharesObligation extends Obligation {

    private Shares shares;

    public RedeemSharesObligation(Shares shares, double amount, int timeLeftToPay) {
        super(shares, amount, timeLeftToPay);
    }

    @Override
    public void fulfil() {
        shares.cashIn(amount);
        System.out.println(shares.getLiabilityParty().getName()+" has fulfilled an obligation to redeem shares and pay " +
                shares.getAssetParty().getName()+
                " an amount "+String.format("%.2f", amount)+".");
    }
}
