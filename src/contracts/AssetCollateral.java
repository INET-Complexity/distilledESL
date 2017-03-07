package contracts;

import agents.Agent;

public class AssetCollateral extends Asset implements CanBeCollateral {

    public AssetCollateral(Agent assetParty, AssetType assetType, AssetMarket assetMarket, double amount) {
        super(assetParty, assetType, assetMarket, amount);
    }

    private double encumberedQuantity;

    @Override
    public void encumber(double quantity) {
        encumberedQuantity += quantity;
    }

    @Override
    public void unEncumber(double quantity) {
        encumberedQuantity -= quantity;
    }

    @Override
    public double getHairCut() {
        return 0;//TODO where do I get the haircuts from? My agent?
    }

    public double getMaxEncumberableQuantity() {
        return getQuantity() - encumberedQuantity;
    }

    public double getMaxEncumberableValue() {return getMaxEncumberableQuantity() * getPrice();}

//    @Override
//    public ArrayList<Action> getAvailableActions(Agent me) {
//        ArrayList<Action> availableActions = super.getAvailableActions(me);
//        if (getMaxEncumberableQuantity() > 0) {
//            availableActions.add(new PledgeAsCollateral(this));
//        }
//
//        return null;
//    }
}
