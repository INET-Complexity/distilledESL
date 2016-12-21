package components.behaviour;

import ESL.agent.Agent;

import java.util.List;

public interface HasBehaviour {
    List<Action> getAvailableActions(Agent agent);
}
