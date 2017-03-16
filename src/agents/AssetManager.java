package agents;

import behaviours.AssetManagerBehaviour;
import behaviours.Behaviour;
import contracts.Contract;
import contracts.Shares;

public class AssetManager extends Agent implements CanIssueShares {

    private AssetManagerBehaviour behaviour;

    public AssetManager(String name) {

        super(name);
        this.behaviour = new AssetManagerBehaviour(this);

    }

    @Override
    public void add(Contract contract) {
        super.add(contract);
    }

    @Override
    public double getNetAssetValue() {
        int nShares = getnShares();

        return (nShares > 0) ? 1.0 * getAssetValue() / nShares : -1.0;
    }

    @Override
    public int getnShares() {
        return mainLedger.getLiabilitiesOfType(Shares.class).stream()
                .mapToInt(contract -> ((Shares) contract).getNumberOfShares()).sum();
    }


    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }
}
