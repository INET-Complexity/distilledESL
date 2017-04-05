package end_to_end_test;

import agents.Agent;
import behaviours.Behaviour;
import economicsl.Message;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;

/**
 * Created by taghawi on 04/04/17.
 */
public class MessageAgent extends Agent {
    private final int odd_or_even;
    private final String name;
    private Agent friend;

    public MessageAgent(String name, Agent friend, int odd_or_even) {
        super(name);
        this.name = name;
        this.odd_or_even = odd_or_even;
        this.friend = friend;
    }

    public void say() {
        message(friend, "hello", odd_or_even);
    }

    public void hear() {
        HashSet<Message> messages = get_messages();
        for (Message message: messages) {
            System.out.print(message.getMessage());
        }
    }

    @Override
    public Behaviour getBehaviour() {
        throw new NotImplementedException();
    }

    public void setFriend(MessageAgent friend) {
        this.friend = friend;
    }
}
