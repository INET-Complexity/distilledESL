package economicsl;


public class Message {
    private final String topic;
    private final Object message;
    public final Agent sender;

    public Message(Agent sender, String topic, Object message) {
        this.sender = sender;
        this.message = message;
        this.topic = topic;
    }

    public Agent getSender() {
        return sender;
    }

    public Object getMessage() {
        return message;
    }

    public String getTopic() {
        return topic;
    }
}
