package contracts.obligations;

import agents.Agent;


public class Message {
    public boolean is_read;
    private final Object message;
    public final Agent sender;

    public Message(Agent sender, Object message) {
        this.sender = sender;
        this.message = message;
        this.is_read = false;
    }

    public Agent getSender() {
        return sender;
    }

    public Object getMessage() {
        this.is_read = true;
        return message;
    }

    public static boolean is_read(Message message) {
        return message.is_read;
    }
}
