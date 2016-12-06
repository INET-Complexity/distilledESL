package components;

import ESL.agent.Agent;

public class FinancialInstitution extends Agent {

    public FinancialInstitution(String name) {
        super(name);
        setInventory(new BalanceSheet());
    }

    public FinancialInstitution() {
        this("");
    }


}
