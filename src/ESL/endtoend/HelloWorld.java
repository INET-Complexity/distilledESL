package ESL.endtoend;

import ESL.agent.Agent;
import ESL.contract.handler.AutomaticContractHandler;
import ESL.inventory.Good;
import sim.engine.SimState;
import ESL.contract.examples.RandomTransfers;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld extends SimState {

    public HelloWorld(long seed) {
	super(seed);
    }

    public void start() {

		super.start(); // reuse the SimState start method

		List<Agent> agents = new ArrayList<Agent>();

		for (int i = 1; i <= 2; i++) {
			Agent a = new Agent("FinancialInstitution " + i);

			// initialize each agent with $100.0
			a.getInventory().add(new Good("cash", 1000.0));

			agents.add(a);
		}

		this.connectedContracts(agents);

    }

    public static void main(String[] args) {
		doLoop(HelloWorld.class, args);
		System.exit(0);
	}

    private void connectedContracts(List<Agent> agents) {

		AutomaticContractHandler handler = new AutomaticContractHandler();

		for (int i = 0; i < agents.size(); i++) {

			for (int j = 0; j < agents.size(); j++) {
			if (i <= j) {
				continue;
			}

			RandomTransfers contract = new RandomTransfers(agents.get(i), agents.get(j), "randomTransfers", this,
				handler);

			}
		}
    }

}
