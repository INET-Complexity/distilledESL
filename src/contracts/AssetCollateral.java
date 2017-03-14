package contracts;

import agents.Agent;
import demos.Parameters;

public class AssetCollateral extends Asset implements CanBeCollateral {

    private double haircut;

    public AssetCollateral(Agent assetParty, AssetType assetType, AssetMarket assetMarket, double amount) {
        super(assetParty, assetType, assetMarket, amount);
        this.haircut = (assetType==AssetType.MBS) ? Parameters.HAIRCUT_MBS
                : (assetType==AssetType.CORPORATE_BONDS) ? Parameters.HAIRCUT_CORPORATE_BONDS
                    : (assetType==AssetType.EQUITIES) ? Parameters.HAIRCUT_EQUITIES
                        : 0.0;
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
        return haircut;
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
