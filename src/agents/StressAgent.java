package agents;

import actions.Action;
import behaviours.Behaviour;
import contracts.Asset;
import contracts.FailedMarginCallException;
import contracts.Repo;
import economicsl.Agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import economicsl.Contract;
import economicsl.accounting.Account;


public abstract class StressAgent extends Agent {
    private double encumberedCash;
    private double equityAtDefault;
    private double lcrAtDefault;

    public StressAgent(String name) {
        super(name);
    }

    /**
     * Operation to cancel a Loan to someone (i.e. cash in a Loan in the Assets side).
     *
     * I'm using this for simplicity but note that this is equivalent to selling an asset.
     * @param amount the amount of loan that is cancelled
     */
    public void pullFunding(double amount, Contract loan) {
        Account loanAccount = getMainLedger().getAccontFromContract(loan);
        // (dr cash, cr asset )
        Account.doubleEntry(getMainLedger().getCashAccount(), loanAccount, amount);
    }

    /**
     * Pre-condition: we have enough liquidity!
     *
     * @param amount the amount to pay back of this loan
     * @param loan   the loan we are paying back
     */
    public void payLiability(double amount, Contract loan) {
        assert (getCash_() - getEncumberedCash() >= amount);
        getMainLedger().payLiability(amount, loan);
    }

    public void sellAssetForValue(Contract asset, double value) {
        getMainLedger().sellAsset(value, asset.getClass());
    }

    public void devalueAsset(Contract asset, double valueLost) {
        getMainLedger().devalueAsset(asset, valueLost);

    }

    public void devalueAssetOfType(Asset.AssetType assetType, double priceLost) {
        getMainLedger().getAssetsOfType(Asset.class).stream()
                .filter(asset -> ((Asset) asset).getAssetType()==assetType)
                .forEach(asset ->
                devalueAsset(asset, ((Asset) asset).getQuantity()*priceLost));

        //Update their prices too
        getMainLedger().getAssetsOfType(Asset.class).stream()
                .filter(asset -> ((Asset) asset).getAssetType()==assetType)
                .forEach(asset -> ((Asset) asset).updatePrice());

    }

    public void appreciateAsset(Contract asset, double valueLost) {
        getMainLedger().appreciateAsset(asset, valueLost);
    }

    public void devalueLiability(Contract asset, double valueLost) {
        getMainLedger().devalueLiability(asset, valueLost);
    }

    public void appreciateLiability(Contract asset, double valueLost) {
        getMainLedger().appreciateLiability(asset, valueLost);
    }

    public double getEncumberedCash() {
        return encumberedCash;
    }

    /**
     * Behavioral stuff; not sure if it should be here
     * @param me the owner of the StressLedger
     * @return an ArrayList of Actions that are available to me at this moment
     */
    public ArrayList<Action> getAvailableActions(Agent me) {
        ArrayList<Action> availableActions = new ArrayList<>();
        for (Contract contract : getMainLedger().getAllAssets()) {
            availableActions.addAll(contract.getAvailableActions(me));
        }

        for (Contract contract : getMainLedger().getAllLiabilities()) {
            availableActions.addAll(contract.getAvailableActions(me));
        }

        return availableActions;
    }

    public abstract Behaviour getBehaviour(); // Make this abstract to force every implementation to provide a behaviour

    public void act() {
        getBehaviour().act();
    }

    public double getLeverage() {
        return (1.0 * getEquityValue() / getAssetValue());
    }

    public double getAssetValue() {
        return getMainLedger().getAssetValue();
    }

    public double getLiabilityValue() {
        return getMainLedger().getLiabilityValue();
    }

    public double getEquityValue() {
        return isAlive() ? getMainLedger().getEquityValue() : equityAtDefault;
    }

    public void printBalanceSheet() {
        System.out.println("\nBalance Sheet of " + getName() + "\n**************************");
        getMainLedger().printBalanceSheet(this);
        System.out.println("\nLeverage ratio: " + String.format("%.2f", 100 * getLeverage()) + "%");
    }

    public void runMarginCalls() throws FailedMarginCallException {
        HashSet<Contract> repoContracts = getMainLedger().getLiabilitiesOfType(Repo.class);
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
        HashSet<Contract> assetsShocked = getMainLedger().getAssetsOfType(Asset.class).stream()
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
        return ( getEquityValue() - getMainLedger().getInitialEquity() ) / getMainLedger().getInitialEquity();
    }

    public void setInitialValues() {
        getMainLedger().setInitialValues();
    }


}
