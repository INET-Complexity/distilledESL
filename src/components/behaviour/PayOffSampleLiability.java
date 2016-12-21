package components.behaviour;

import components.agents.FinancialInstitution;
import components.items.SampleLiability;

public class PayOffSampleLiability implements Action {

    public void perform(FinancialInstitution agent, double amount) {
        if (agent.getInventory().getAllGoodEntries().get("SampleLiability") < amount) {
            System.out.println("Strange. I'm trying to pay off more liability than I have.");
        } else {
            agent.remove(new SampleLiability(amount));
        }
    }
}
