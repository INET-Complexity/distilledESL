package components;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;

public class FinancialInstitution extends Agent {

    public FinancialInstitution(String name) {
        super(name);
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

    public FinancialInstitution() {
        this("");
    }


}
