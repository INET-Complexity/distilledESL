package components.behaviour;

import components.agents.FinancialInstitution;

/**
 * Created by eva on 13/12/2016.
 */
public abstract class Behaviour {

    private FinancialInstitution agent;

    public Behaviour(FinancialInstitution agent){
        this.agent=agent;
    }



}
