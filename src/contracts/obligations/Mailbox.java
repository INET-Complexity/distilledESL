package contracts.obligations;

import demos.Model;
import demos.Parameters;

import economicsl.GoodMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Mailbox {
    private HashSet<Obligation> obligation_unopened;
    private HashSet<Obligation> obligation_outbox;
    private HashSet<Obligation> obligation_inbox;
    private HashSet<Message> message_inbox;
    private HashSet<Message> message_unopened;
    public HashSet<GoodMessage> goods_inbox;


    public Mailbox() {
        this.obligation_unopened = new HashSet<>();
        this.obligation_outbox = new HashSet<>();
        this.obligation_inbox = new HashSet<>();
        this.message_unopened = new HashSet<>();
        this.message_inbox = new HashSet<>();
        this.goods_inbox = new HashSet<>();
    }


    public void receiveObligation(Obligation obligation) {

        obligation_unopened.add(obligation);

        System.out.println("Obligation sent. "+obligation.getFrom().getName() +
                " must pay "+obligation.getAmount()+" to "+obligation.getTo().getName()
                +" on timestep "+obligation.getTimeToPay());
    }

    public void receiveMessage(Message msg) {
        message_unopened.add(msg);
        //System.out.println("Message sent. " + msg.getSender().getName() +
        //        " message: " + msg.getMessage());
    }

    public void receiveGoodMessage(GoodMessage good_message) {
        System.out.println(good_message);
        goods_inbox.add(good_message);
        //System.out.println("Message sent. " + msg.getSender().getName() +
        //        " message: " + msg.getMessage());
    }


    public void addToObligationOutbox(Obligation obligation) {
        obligation_outbox.add(obligation);
    }

    public double getMaturedObligations() {
        return obligation_inbox.stream()
                .filter(Obligation::isDue)
                .filter(obligation -> ! (obligation.isFulfilled()))
                .mapToDouble(Obligation::getAmount).sum();
    }

    public double getAllPendingObligations() {
        return obligation_inbox.stream()
                .filter(obligation -> ! obligation.isFulfilled())
                .mapToDouble(Obligation::getAmount).sum();
    }

    public double getPendingPaymentsToMe() {
        return obligation_outbox.stream()
                .filter(Obligation::isFulfilled)
                .mapToDouble(Obligation::getAmount)
                .sum();
    }

    public void fulfilAllRequests() {
        for (Obligation obligation : obligation_inbox) {
            if (! obligation.isFulfilled() ) obligation.fulfil();
        }
    }

    public void fulfilMaturedRequests() {
        for (Obligation obligation : obligation_inbox) {
            if (obligation.isDue() && ! obligation.isFulfilled()) {
                obligation.fulfil();
            }
        }
    }

    public void step() {
        // Remove all fulfilled requests
        obligation_inbox.removeIf(Obligation::isFulfilled);
        obligation_outbox.removeIf(Obligation::isFulfilled);

        // Remove all requests from agents who have defaulted.
        // TODO should be in model not in the library
        obligation_outbox.removeIf(obligation -> (!(obligation.getFrom().isAlive())));

        // Move all messages in the obligation_unopened to the obligation_inbox
        obligation_inbox.addAll(
                obligation_unopened.stream()
                        .filter(Obligation::hasArrived)
                        .collect(Collectors.toCollection(HashSet::new)));

        obligation_unopened.removeIf(Obligation::hasArrived);


        // Remove all fulfilled requests
        message_inbox.removeIf(Message::is_read);

        // Move all messages in the obligation_unopened to the obligation_inbox
        message_inbox.addAll(
                message_unopened.stream()  // what is this
                        .collect(Collectors.toCollection(HashSet::new)));

        message_unopened = new HashSet<>();


        // Remove all fulfilled requests
        assert(goods_inbox.isEmpty());

        // Move all messages in the obligation_unopened to the obligation_inbox
    }


    public ArrayList<Double> getCashCommitments() {
        ArrayList<Double> cashCommitments = new ArrayList<>(Collections.nCopies(Parameters.TIMESTEPS_TO_PAY * 3, 0.0));

        for (Obligation obligation : obligation_inbox) {
            if (!(obligation.isFulfilled())) {
                int index = obligation.getTimeToPay() - Model.getTime() - 1;
                cashCommitments.set(index, cashCommitments.get(index) + obligation.getAmount());
            }
        }
        return cashCommitments;
    }

    public ArrayList<Double> getCashInflows() {
        ArrayList<Double> cashInflows = new ArrayList<>(Collections.nCopies(Parameters.TIMESTEPS_TO_PAY * 3, 0.0));

        for (Obligation obligation : obligation_outbox) {
            if (!(obligation.isFulfilled())) {
                int index = obligation.getTimeToReceive() - Model.getTime() - 1;
                cashInflows.set(index, cashInflows.get(index) + obligation.getAmount());
            }
        }
        return cashInflows;
    }

    public void printMailbox() {
        if (obligation_unopened.isEmpty() && obligation_inbox.isEmpty() && obligation_outbox.isEmpty()) System.out.println("\nMailbox is empty.");
        else {
            System.out.println("\nMailbox contents:");
            if (!obligation_unopened.isEmpty()) System.out.println("Unopened messages:");
            obligation_unopened.forEach(Obligation::printObligation);

            if (!obligation_inbox.isEmpty()) System.out.println("Inbox:");
            obligation_inbox.forEach(Obligation::printObligation);

            if (!obligation_outbox.isEmpty()) System.out.println("Outbox:");
            obligation_outbox.forEach(Obligation::printObligation);
            System.out.println();
        }
    }


    public HashSet<Message> getMessageInbox() {
        return message_inbox;
    }
}

