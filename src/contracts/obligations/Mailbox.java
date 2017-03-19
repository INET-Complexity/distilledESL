package contracts.obligations;

import demos.BoEDemo;
import demos.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Mailbox {
    private HashSet<Obligation> unopenedMessages;
    private HashSet<Obligation> outbox;
    private HashSet<Obligation> inbox;


    public Mailbox() {
        this.unopenedMessages = new HashSet<>();
        this.outbox = new HashSet<>();
        this.inbox = new HashSet<>();
    }


    public void receiveMessage(Obligation obligation) {

        unopenedMessages.add(obligation);

        System.out.println("Obligation sent. "+obligation.getFrom().getName() +
                " must pay "+obligation.getAmount()+" to "+obligation.getTo().getName()
                +" on timestep "+obligation.getTimeToPay());
    }

    public void addToOutbox(Obligation obligation) {
        outbox.add(obligation);
    }

    public double getMaturedObligations() {
        return inbox.stream()
                .filter(Obligation::isDue)
                .filter(obligation -> ! (obligation.isFulfilled()))
                .mapToDouble(Obligation::getAmount).sum();
    }

    public double getAllPendingObligations() {
        return inbox.stream()
                .filter(obligation -> ! obligation.isFulfilled())
                .mapToDouble(Obligation::getAmount).sum();
    }

    public double getPendingPaymentsToMe() {
        return outbox.stream()
                .filter(Obligation::isFulfilled)
                .mapToDouble(Obligation::getAmount)
                .sum();
    }

    public void fulfilAllRequests() {
        for (Obligation obligation : inbox) {
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

    public void step() {
        // Remove all fulfilled requests
        inbox.removeIf(Obligation::isFulfilled);
        outbox.removeIf(Obligation::isFulfilled);

        // Move all messages in the unopenedMessages to the inbox
        inbox.addAll(
                unopenedMessages.stream()
                        .filter(Obligation::hasArrived)
                        .collect(Collectors.toCollection(HashSet::new)));

        unopenedMessages.removeIf(Obligation::hasArrived);
    }


    public ArrayList<Double> getCashCommitments() {
        ArrayList<Double> cashCommitments = new ArrayList<>(Collections.nCopies(Parameters.TIMESTEPS_TO_PAY * 3, 0.0));

        for (Obligation obligation : inbox) {
            if (!(obligation.isFulfilled())) {
                int index = obligation.getTimeToPay() - BoEDemo.getTime() - 1;
                cashCommitments.set(index, cashCommitments.get(index) + obligation.getAmount());
            }
        }
        return cashCommitments;
    }

    public ArrayList<Double> getCashInflows() {
        ArrayList<Double> cashInflows = new ArrayList<>(Collections.nCopies(Parameters.TIMESTEPS_TO_PAY * 3, 0.0));

        for (Obligation obligation : outbox) {
            if (!(obligation.isFulfilled())) {
                int index = obligation.getTimeToReceive() - BoEDemo.getTime() - 1;
                cashInflows.set(index, cashInflows.get(index) + obligation.getAmount());
            }
        }
        return cashInflows;
    }

    public void printMailbox() {
        if (unopenedMessages.isEmpty() && inbox.isEmpty() && outbox.isEmpty()) System.out.println("\nMailbox is empty.");
        else {
            System.out.println("\nMailbox contents:");
            if (!unopenedMessages.isEmpty()) System.out.println("Unopened messages:");
            unopenedMessages.forEach(Obligation::printObligation);

            if (!inbox.isEmpty()) System.out.println("Inbox:");
            inbox.forEach(Obligation::printObligation);

            if (!outbox.isEmpty()) System.out.println("Outbox:");
            outbox.forEach(Obligation::printObligation);
            System.out.println();
        }
    }
}
