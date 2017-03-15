package agents;

import behaviours.AssetManagerBehaviour;
import behaviours.Behaviour;
import contracts.Contract;
import contracts.Shares;

public class AssetManager extends Agent implements CanIssueShares {

    private AssetManagerBehaviour behaviour;
    private int nShares;
    private boolean nSharesModified;

    public AssetManager(String name) {

        super(name);
        this.behaviour = new AssetManagerBehaviour(this);
        this.nSharesModified = true;
    }

    @Override
    public void add(Contract contract) {
        super.add(contract);
        if (contract instanceof Shares) nSharesModified = true;
    }

    @Override
    public double getNetAssetValue() {
        if (nSharesModified) nShares = mainLedger.getLiabilitiesOfType(Shares.class).stream()
                .mapToInt(contract -> ((Shares) contract).getNumberOfShares()).sum();
        nSharesModified = false;

        return 1.0 * getAssetValue() / nShares;
    }


    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }
}
