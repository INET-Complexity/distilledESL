package doubleEntryComponents;

import java.util.HashSet;

public class Simulation {

    public Simulation() {
        agents = new HashSet<>();
    }
    public HashSet<Agent> agents;

    public void addAgent(Agent a) {
        agents.add(a);
    }
}
