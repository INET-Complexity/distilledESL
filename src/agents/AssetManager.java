package agents;

import behaviours.AssetManagerBehaviour;
import behaviours.Behaviour;
import contracts.Shares;

public class AssetManager extends StressAgent implements CanIssueShares {

    private AssetManagerBehaviour behaviour;
    private int nShares;

    public AssetManager(String name) {
        super(name);
        this.behaviour = new AssetManagerBehaviour(this);

    }

    public void updateValueOfAllShares() {
        mainLedger.getLiabilitiesOfType(Shares.class).forEach(shares -> ((Shares) shares).updateValue());
    }

    public Shares issueShares(StressAgent owner, int quantity) {
        nShares += quantity;
        if (nShares - quantity > 0) {
            updateValueOfAllShares();
        }
        return new Shares(owner, this, quantity, getNetAssetValue());
    }

    @Override
    public double getNetAssetValue() {
        assert(nShares > 0);
        return 1.0 * getAssetValue() / nShares;
    }

    @Override
    public int getnShares() {
        return nShares;
    }

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public void step() {
        super.step();
        updateValueOfAllShares();

    }

    @Override
    public double getEquityValue() {
        return super.getAssetValue();
    }

    @Override
    public double getLCR() {
        return getCash_() - getEncumberedCash();
    }

    @Override
    public double getEquityLoss() {
        return 0.0;
    }
}
