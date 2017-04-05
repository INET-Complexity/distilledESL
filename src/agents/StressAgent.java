package agents;

import actions.Action;
import behaviours.Behaviour;
import contracts.Asset;
import contracts.Contract;
import contracts.FailedMarginCallException;
import contracts.Repo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class StressAgent extends Agent {
    private double encumberedCash;
    private double equityAtDefault;
    private double lcrAtDefault;

    public StressAgent(String name) {
        super(name);
    }

    public void pullFunding(double amount, Contract loan) {
        mainLedger.pullFunding(amount, loan);
    }

    /**
     * Pre-condition: we have enough liquidity!
     *
     * @param amount the amount to pay back of this loan
     * @param loan   the loan we are paying back
     */
    public void payLiability(double amount, Contract loan) {
        assert (getCash_() - getEncumberedCash() >= amount);
        mainLedger.payLiability(amount, loan);
    }

    public void sellAssetForValue(Contract asset, double value) {
        mainLedger.sellAsset(value, asset.getClass());
    }

    public void devalueAsset(Contract asset, double valueLost) {
        mainLedger.devalueAsset(asset, valueLost);

    }

    public void devalueAssetOfType(Asset.AssetType assetType, double priceLost) {
        mainLedger.getAssetsOfType(Asset.class).stream()
                .filter(asset -> ((Asset) asset).getAssetType()==assetType)
                .forEach(asset ->
                devalueAsset(asset, ((Asset) asset).getQuantity()*priceLost));

        //Update their prices too
        mainLedger.getAssetsOfType(Asset.class).stream()
                .filter(asset -> ((Asset) asset).getAssetType()==assetType)
                .forEach(asset -> ((Asset) asset).updatePrice());

    }

    public void appreciateAsset(Contract asset, double valueLost) {
        mainLedger.appreciateAsset(asset, valueLost);
    }

    public void devalueLiability(Contract asset, double valueLost) {
        mainLedger.devalueLiability(asset, valueLost);
    }

    public void appreciateLiability(Contract asset, double valueLost) {
        mainLedger.appreciateLiability(asset, valueLost);
    }

    public double getEncumberedCash() {
        return encumberedCash;
    }

    public ArrayList<Action> getAvailableActions(Agent me) {
        return mainLedger.getAvailableActions(this);
    }

    public abstract Behaviour getBehaviour(); // Make this abstract to force every implementation to provide a behaviour

    public void act() {
        getBehaviour().act();
    }

    public double getLeverage() {
        return (1.0 * getEquityValue() / getAssetValue());
    }

    public double getAssetValue() {
        return mainLedger.getAssetValue();
    }

    public double getLiabilityValue() {
        return mainLedger.getLiabilityValue();
    }

    public double getEquityValue() {
        return isAlive() ? mainLedger.getEquityValue() : equityAtDefault;
    }

    public void printBalanceSheet() {
        System.out.println("\nBalance Sheet of " + getName() + "\n**************************");
        mainLedger.printBalanceSheet(this);
        System.out.println("\nLeverage ratio: " + String.format("%.2f", 100 * getLeverage()) + "%");
    }

    public void runMarginCalls() throws FailedMarginCallException {
        HashSet<Contract> repoContracts = mainLedger.getLiabilitiesOfType(Repo.class);
        for (Contract contract : repoContracts) {
            Repo repo = (Repo) contract;
            repo.marginCall(); // Throws exception if it fails.
        }
    }

    public void triggerDefault() {
        alive = false;
        equityAtDefault = getEquityValue();
        lcrAtDefault = getLCR();
        System.out.println("Trigger default!");
    }

    public void encumberCash(double amount) {
        assert (getCash_() - getEncumberedCash() >= amount);

        encumberedCash += amount;
    }

    public void unencumberCash(double amount) {
        assert (encumberedCash >= amount);
        encumberedCash -= amount;
    }

    public void receiveShockToAsset(Asset.AssetType assetType, double fractionLost) {
        HashSet<Contract> assetsShocked = mainLedger.getAssetsOfType(Asset.class).stream()
                .filter(asset -> ((Asset) asset).getAssetType() == assetType)
                .collect(Collectors.toCollection(HashSet::new));

        if (!(assetsShocked.isEmpty())) {
            System.out.println(getName() + " received a shock!! Asset type " + assetType + " lost "
                    + String.format("%.2f", fractionLost * 100.0) + "% of value.");
            for (Contract asset : assetsShocked) {
                devalueAsset(asset, asset.getValue(null) * fractionLost);
                ((Asset) asset).updatePrice();
            }
        }
    }

    public double getMaturedObligations() {
        return obligationsAndGoodsMailbox.getMaturedObligations();
    }

    public double getAllPendingObligations() {
        return obligationsAndGoodsMailbox.getAllPendingObligations();
    }

    public double getPendingPaymentsToMe() {
        return obligationsAndGoodsMailbox.getPendingPaymentsToMe();
    }

    public void fulfilAllRequests() {
        obligationsAndGoodsMailbox.fulfilAllRequests();
    }

    public void fulfilMaturedRequests() {
        obligationsAndGoodsMailbox.fulfilMaturedRequests();
    }

    public ArrayList<Double> getCashCommitments() {
        return obligationsAndGoodsMailbox.getCashCommitments();
    }

    public ArrayList<Double> getCashInflows() {
        return obligationsAndGoodsMailbox.getCashInflows();
    }

    public double getLCR() {
        return isAlive()? getCash_() - getEncumberedCash() : lcrAtDefault;
    }

    public double getLcrAtDefault() {
        return lcrAtDefault;
    }

    public double getEquityLoss() {
        return ( getEquityValue() - mainLedger.getInitialEquity() ) / mainLedger.getInitialEquity();
    }

    public void setInitialValues() {
        mainLedger.setInitialValues();
    }


}
