package org.economicsl;

import java.util.HashSet;
import java.util.stream.Collectors;

public class Mailbox {

    private HashSet<Message> message_inbox;
    private HashSet<Message> message_unopened;


    public Mailbox() {
        this.message_unopened = new HashSet<Message>();
        this.message_inbox = new HashSet<Message>();
    }

    public void receiveMessage(Message msg) {
        message_unopened.add(msg);
        //System.out.println("ObligationMessage sent. " + msg.getSender().getName() +
        //        " message: " + msg.getMessage());
    }

    public void step() {
        // Move all messages in the obligation_unopened to the obligation_inbox
        message_inbox.addAll(
                message_unopened.stream()  // what is this
                        .collect(Collectors.toCollection(HashSet::new)));

        message_unopened = new HashSet<Message>();

        // Move all messages in the obligation_unopened to the obligation_inbox
    }

    public HashSet<Message> get_massages() {
        HashSet<Message> messages = message_inbox;
        message_inbox = new HashSet<>();
        return messages;
    }

    public HashSet<Message> get_massages(String topic) {
        //TODO this is probably fucking inefficient
        HashSet<Message> filtered_messages = new HashSet<Message>();
        for (Message message : message_inbox) {
            if (message.getTopic() == topic) {
                filtered_messages.add(message);
                message_inbox.remove(message);
            }
        }
        return filtered_messages;
    }
}

