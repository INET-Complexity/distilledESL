package components.institutions;

import ESL.agent.Agent;
import components.behaviour.Behaviour;
import components.markets.Market;
import components.markets.StockMarket;

/**
 * Created by eva on 13/12/2016.
 */
public class FinancialInstitution extends Agent {
    private Behaviour behaviour;

    public FinancialInstitution(String name){
        super(name);
    }

    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour() {
        return this.behaviour;
    }






}
