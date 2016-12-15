package components.behaviour;

import components.institutions.Bank;
import components.institutions.FinancialInstitution;

/**
 * Created by eva on 13/12/2016.
 */
public abstract class Behaviour {

    private FinancialInstitution agent;

    public Behaviour(FinancialInstitution agent){
        this.agent=agent;
    }



}
