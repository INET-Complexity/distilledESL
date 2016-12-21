package components.agents;

import ESL.agent.Agent;
import components.behaviour.Behaviour;

import java.util.Map;

public class FinancialInstitution extends Agent {

    public FinancialInstitution(String name){
        super(name);
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
