package contracts.obligations;

import java.util.Collection;
import java.util.HashSet;

public class Mailbox {
    private HashSet<Obligation> inbox;
    private HashSet<Obligation> outbox;
    private HashSet<Obligation> receivedMessages;


    public Mailbox() {
        this.inbox = new HashSet<>();
        this.outbox = new HashSet<>();
        this.receivedMessages = new HashSet<>();
    }


    public void addToInbox(Obligation obligation) {

        inbox.add(obligation);
        System.out.println("Obligation sent from "+obligation.getFrom().getName() +
                " to "+obligation.getTo().getName());
    }

    public void addToOutbox(Obligation obligation) {
        outbox.add(obligation);
    }

    private void addAllToReceivedMessages(Collection<Obligation> obligations) {
        receivedMessages.addAll(obligations);

        for (Obligation obligation : obligations) {
            System.out.println("Obligation of type "+obligation.getClass().getName()+" received: from " +
                    obligation.getFrom() + " for an amount "+obligation.getAmount());
        }
    }

    public double getMaturedObligations() {
        return receivedMessages.stream()
                .filter(Obligation::isDue)
                .filter(obligation -> ! obligation.isFulfilled())
                .mapToDouble(Obligation::getAmount).sum();
    }

    public double getAllPendingObligations() {
        return receivedMessages.stream()
                .filter(obligation -> ! obligation.isFulfilled())
                .mapToDouble(Obligation::getAmount).sum();
    }

    public void fulfilAllRequests() {
        for (Obligation obligation : receivedMessages) {
            if (! obligation.isFulfilled() ) obligation.fulfil();
        }
    }

    public void fulfilMaturedRequests() {
        for (Obligation obligation : inbox) {
            if (obligation.isDue() && ! obligation.isFulfilled()) {
                obligation.fulfil();
            }
        }
    }

    public void tick() {
        // Remove all fulfilled requests
        receivedMessages.removeIf(Obligation::isFulfilled);

        // Move all messages in the inbox to the receivedMessages
        addAllToReceivedMessages(inbox);
        inbox.clear();

        for (Obligation obligation : inbox) {
            obligation.tick();
        }

    }

}
