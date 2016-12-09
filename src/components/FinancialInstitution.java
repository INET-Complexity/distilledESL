package components;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Action;
import components.behaviour.Behaviour;

import java.util.ArrayList;

public class FinancialInstitution extends Agent {
    private Behaviour behaviour;

    public FinancialInstitution(String name) {
        super(name);
    }
    public FinancialInstitution() {
        this("");
    }


    public void add(Contract contract) {
        try {
            getInventory().add(contract);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void add(Good good) {
        try{
            getInventory().add(good);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void setBehaviour(Behaviour behaviour) {
        this.behaviour=behaviour;
    }

    public ArrayList<Action> getAvailableActions() {
        // TODO: Define my available actions
        return null;
    }
}
