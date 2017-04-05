package agents;

import accounting.Ledger;
import actions.Action;
import behaviours.Behaviour;
import contracts.Asset;
import contracts.Contract;
import contracts.FailedMarginCallException;
import contracts.Repo;
import contracts.obligations.*;

import economicsl.GoodMessage;
import economicsl.Mailbox;
import economicsl.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class Agent {
    Ledger mainLedger;
    private String name;
    private boolean alive = true;
    private double encumberedCash;
    protected ObligationsAndGoodsMailbox obligationsAndGoodsMailbox;
    protected Mailbox mailbox;
    private double equityAtDefault;
    private double lcrAtDefault;

    public Agent(String name) {
        this.name = name;
        mainLedger = new Ledger(this);
        this.obligationsAndGoodsMailbox = new ObligationsAndGoodsMailbox();
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
        return mainLedger.getCash() - encumberedCash;
    }

    public double getTotalCash() {
        return mainLedger.getCash();
    }

    public double getEncumberedCash() {
        return encumberedCash;
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

    public boolean isAlive() {
        return alive;
    }

    public void triggerDefault() {
        alive = false;
        equityAtDefault = getEquityValue();
        lcrAtDefault = getLCR();
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
                devalueAsset(asset, asset.getValue(null) * fractionLost);
                ((Asset) asset).updatePrice();
            }
        }
    }

    public void step() {
        for (GoodMessage good_message: obligationsAndGoodsMailbox.goods_inbox) {
            getMainLedger().addGoods(good_message.good_name, good_message.amount, good_message.value);
        }
        obligationsAndGoodsMailbox.goods_inbox.clear();
        obligationsAndGoodsMailbox.step();
        mailbox.step();
    }

    public void sendObligation(Agent recipient, Obligation obligation) {
        recipient.receiveObligation(obligation);
        obligationsAndGoodsMailbox.addToObligationOutbox(obligation);
    }

    public void sendObligation(Agent recipient, Object message) {
        ObligationMessage msg = new ObligationMessage(this, message);
        recipient.receiveMessage(msg);
    }

    public void receiveObligation(Obligation obligation) {
        obligationsAndGoodsMailbox.receiveObligation(obligation);
    }

    public void receiveMessage(ObligationMessage msg) {
        obligationsAndGoodsMailbox.receiveMessage(msg);
    }

    public void receiveGoodMessage(GoodMessage good_message) {
        obligationsAndGoodsMailbox.receiveGoodMessage(good_message);
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

    public void printMailbox() {
        obligationsAndGoodsMailbox.printMailbox();
    }

    public double getLCR() {
        return isAlive()? getCash() : lcrAtDefault;
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

    public Message message(Agent receiver, String topic, Object content) {
        Message message = new Message(this, topic, content);
        receiver.receiveMessage(message);
        return message;
    }

    private void receiveMessage(Message message) {
        mailbox.receiveMessage(message);
    }

    public HashSet<Message> get_messages() {
        return mailbox.get_massages();
    }


    private HashSet<Message> get_messages(String topic) {
        return mailbox.get_massages(topic);
    }
}
