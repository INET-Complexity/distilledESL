package components.agents;

import ESL.agent.Agent;
import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.Behaviour;

import java.util.Map;

public class FinancialInstitution extends Agent {

    public FinancialInstitution(String name){
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

    public void remove(Good good) {
        try {
            getInventory().remove(good);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(Contract contract) {
        try {
            getInventory().remove(contract);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour() {
        return this.behaviour;
    }

    public void setGlobalParameters(Map<Object, Object> globalParameters) {
        this.globalParameters = globalParameters;
    }

    public Map<Object, Object> getGlobalParameters() {
        return globalParameters;
    }

    public double getAssetValue() {
        return this.getInventory().asset_value(globalParameters, this);
    }

    public double getLiabilityValue() {
        return this.getInventory().liability_value(globalParameters,this);
    }


    private Behaviour behaviour;
    private Map<Object, Object> globalParameters;


}
