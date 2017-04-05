package contracts.obligations;

import economicsl.Agent;


public class ObligationMessage {
    public boolean is_read;
    private final Object message;
    public final Agent sender;

    public ObligationMessage(Agent sender, Object message) {
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

    public static boolean is_read(ObligationMessage obligationMessage) {
        return obligationMessage.is_read;
    }
}
