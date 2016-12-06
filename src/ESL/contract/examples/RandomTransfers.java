package ESL.contract.examples;

import ESL.agent.Agent;
import ESL.contract.MasonScheduledContracts;
import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.contract.obligation.ScheduledObligation;
import ESL.inventory.Good;
import ec.util.MersenneTwisterFast;
import sim.engine.SimState;

public class RandomTransfers extends MasonScheduledContracts {

	private Agent agent1;
	private Agent agent2;
	private Boolean active;

	
	public RandomTransfers(Agent agent1, Agent agent2, String name, SimState state, ContractHandler handler) {
		super(name, state, handler);
		this.agent1 = agent1;
		this.agent2 = agent2;
		active = true;
		//during initialization, schedule the first Obligation
		this.scheduleEvent(this.requestNextObligation(state), state);
	
	}

	@Override
	public void handleResponse(ObligationResponse response) {
		
		//if someone couldn't handle the transfer, cancel the contract. 
		if (!response.getFilled()) {
			active = false;
		} else {
			Good g = (Good) response.getObligation().getWhat();
			Obligation o = response.getObligation();
			
			System.out.println(o.getFrom().getName() + " sent " + o.getFrom().getName() + 
					" $" + g.getQuantity() + ". As such, " + o.getFrom().getName() +
					" has $" + o.getFrom().getInventory().getAllGoodEntries().get("cash").doubleValue() + 
					" and " + o.getTo().getName() + " has $" + 
					o.getTo().getInventory().getAllGoodEntries().get("cash").doubleValue());
			
		}
	}

	@Override
	public ScheduledObligation requestNextObligation(SimState state) {
		
		if (!active) {
			return null;
		}
		
		//randomly assign the amount to trade from 0 - 100.
		MersenneTwisterFast r = state.random;
		double amount = r.nextDouble() * 10.0;
		Good trade = new Good("cash", amount);
		
		Obligation o = new Obligation(agent1, agent2,trade);
		
		// There is a coin flip on who trades with whom
		if (r.nextBoolean()) {
			o = new Obligation(agent2, agent1,trade);
		}
		
		//the next action occurs in the next 1-5 rounds
		r = state.random;
		Double time = state.schedule.getTime() + r.nextInt(5) + 1;
		
		// delete, for print
		Good g = (Good) o.getWhat();
		System.out.println(o.getFrom().getName() +
				" has $" + o.getFrom().getInventory().getAllGoodEntries().get("cash").doubleValue() + 
				" and " + o.getTo().getName() + " has $" + 
				o.getTo().getInventory().getAllGoodEntries().get("cash").doubleValue() + 
				" and " + o.getFrom().getName() + " is scheduled to send " + o.getTo().getName() + 
				" $" + g.getQuantity() + " in " + time + " rounds.");
		
		return new ScheduledObligation(o, time);
	}

}
