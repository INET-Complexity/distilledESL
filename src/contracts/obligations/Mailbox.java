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


    public void addToInbox(Obligation obligation) {

        unopenedMessages.add(obligation);
        System.out.println("Obligation sent from "+obligation.getFrom().getName() +
                " to "+obligation.getTo().getName());
    }

    public void addToOutbox(Obligation obligation) {
        outbox.add(obligation);
    }

    private void addAllToReceivedMessages() {
        HashSet<Obligation> obligationsThatHaveArrived = unopenedMessages.stream()
                .filter(Obligation::hasArrived)
                .collect(Collectors.toCollection(HashSet::new));

        for (Obligation obligation : obligationsThatHaveArrived) {
            System.out.println("Obligation of type "+obligation.getClass().getName()+
            " has arrived from "+obligation.getFrom().getName());
        }

        inbox.addAll(obligationsThatHaveArrived);
    }

    public double getMaturedObligations() {
        return inbox.stream()
                .filter(Obligation::isDue)
                .filter(obligation -> ! obligation.isFulfilled())
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

        // Move all messages in the unopenedMessages to the inbox
        addAllToReceivedMessages();
        unopenedMessages.clear();

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
                System.out.println("Obligation from "+obligation.getFrom().getName()+" to "+obligation.getTo().getName() +
                "for timestep "+obligation.getTimeToPay());
                System.out.println("The current timestep is "+BoEDemo.getTime());


                int index = obligation.getTimeToPay() - BoEDemo.getTime(); //Todo: important! TimeToPay + 1
                cashInflows.set(index, cashInflows.get(index) + obligation.getAmount());
            }
        }
        return cashInflows;
    }

}
