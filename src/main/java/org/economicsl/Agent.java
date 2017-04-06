package org.economicsl;

import org.economicsl.accounting.Ledger;
import org.economicsl.obligations.Obligation;
import org.economicsl.obligations.ObligationMessage;
import org.economicsl.obligations.ObligationsAndGoodsMailbox;

import java.util.HashSet;

/**
 * Created by taghawi on 05/04/17.
 */
public class Agent {
    protected String name;
    private Simulation simulation;
    protected boolean alive = true;
    protected ObligationsAndGoodsMailbox obligationsAndGoodsMailbox;
    protected Mailbox mailbox;
    Ledger mainLedger;


    public Agent(String name, Simulation simulation) {
        this.name = name;
        this.simulation = simulation;
        this.mailbox = new Mailbox();
        mainLedger = new Ledger(this);
        this.obligationsAndGoodsMailbox = new ObligationsAndGoodsMailbox();
    }

    public String getName() {
        return name;
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

    public int getTime() {
        return this.simulation.getTime();
    }

    public void addCash(double amount) {
        mainLedger.addCash(amount);
    }

    public double getCash_() {
        return mainLedger.getCash();
    }

    public double getTotalCash() {
        return mainLedger.getCash();
    }

    public Ledger getMainLedger() {
        return mainLedger;
    }

    public boolean isAlive() {
        return alive;
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

    public void printMailbox() {
        obligationsAndGoodsMailbox.printMailbox();
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

    public HashSet<Obligation> get_obligation_inbox() {
        return obligationsAndGoodsMailbox.getObligation_inbox();
    }

    public HashSet<Obligation> get_obligation_outbox() {
        return obligationsAndGoodsMailbox.getObligation_outbox();
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
