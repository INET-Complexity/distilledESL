package agents;

import accounting.Ledger;
import actions.Action;
import behaviours.Behaviour;
import contracts.Asset;
import contracts.Contract;
import contracts.FailedMarginCallException;
import contracts.Repo;
import contracts.obligations.Mailbox;
import contracts.obligations.Obligation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class Agent {
    Ledger mainLedger;
    private String name;
    private boolean alive = true;
    private double encumberedCash;
    private Mailbox mailbox;


    public Agent(String name) {
        this.name = name;
        mainLedger = new Ledger(this);
        this.mailbox = new Mailbox();
    }

    public String getName() {
        return name;
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
        assert (getCash() >= amount);
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

    public void add(Contract contract) {
        if (contract.getAssetParty() == this) {
            // This contract is an asset for me.
            mainLedger.addAsset(contract);
        } else if (contract.getLiabilityParty() == this) {
            // This contract is a liability for me
            mainLedger.addLiability(contract);
        }
    }

    public void addCash(double amount) {
        mainLedger.addCash(amount);
    }

    public double getCash() {
        // Todo: important! We only return the unencumbered cash.
        return mainLedger.getCash() - encumberedCash;
    }


    public ArrayList<Action> getAvailableActions(Agent me) {
        return mainLedger.getAvailableActions(this);
    }

    public Ledger getMainLedger() {
        return mainLedger;
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
        return isAlive() ? mainLedger.getEquityValue() : 0.0;
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

    public boolean isAlive() {
        return alive;
    }

    public void triggerDefault() {
        alive = false;
        System.out.println("Trigger default!");
    }

    public void encumberCash(double amount) {
        assert (getCash() >= amount);

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
                devalueAsset(asset, asset.getValue() * fractionLost);
            }
        }
    }

    public void step() {
        mailbox.step();
    }

    public void sendMessage(Agent recipient, Obligation obligation) {
        recipient.receiveMessage(obligation);
        mailbox.addToOutbox(obligation);
    }

    public void receiveMessage(Obligation obligation) {
        mailbox.receiveMessage(obligation);
    }

    public double getMaturedObligations() {
        return mailbox.getMaturedObligations();
    }

    public double getAllPendingObligations() {
        return mailbox.getAllPendingObligations();
    }

    public double getPendingPaymentsToMe() {
        return mailbox.getPendingPaymentsToMe();
    }

    public void fulfilAllRequests() {
        mailbox.fulfilAllRequests();
    }

    public void fulfilMaturedRequests() {
        mailbox.fulfilMaturedRequests();
    }

    public ArrayList<Double> getCashCommitments() {
        return mailbox.getCashCommitments();
    }

    public ArrayList<Double> getCashInflows() {
        return mailbox.getCashInflows();
    }

    public void printMailbox() {
        mailbox.printMailbox();
    }

    public abstract double getLCR();


}
