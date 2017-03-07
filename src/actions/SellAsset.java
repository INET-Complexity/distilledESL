package actions;

import agents.Bank;
import contracts.Asset;

public class SellAsset extends Action {

    private Asset asset;

    public SellAsset(Asset asset) {
        this.asset = asset;
        setAmount(0.0);
    }

    @Override
    public void perform() {
        Bank owner = (Bank) asset.getAssetParty();
        // changes the contract
        asset.sellAmount(getAmount());
        // changes the accounts
        owner.getMainLedger().sellAsset(getAmount(), asset.getClass());
    }

    @Override
    public double getMax() {
        return asset.getValue();
    }

    @Override
    public void print() {
        System.out.println("Sell Asset action by "+asset.getAssetParty().getName()+" -> asset type: "+asset.getAssetType() +", amount: "+String.format( "%.2f", getAmount()));
    }

    public String getName() {
        return "Sell Asset of type "+ asset.getAssetType();
    }
}
