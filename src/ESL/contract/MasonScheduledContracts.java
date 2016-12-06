package ESL.contract;

import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.FillObligation;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.contract.obligation.ScheduledObligation;
import sim.engine.SimState;
import sim.engine.Steppable;

public abstract class MasonScheduledContracts extends HandledContracts implements Steppable {

	private Obligation nextObligation;
	
	public MasonScheduledContracts(String name, SimState state, ContractHandler handler) {
		super(name,handler);
	}
	
	public void step(SimState state) {
		
		
		//request the fulfillment of the current Obligation
		FillObligation fill = new FillObligation(nextObligation);
		
		//receive a response back about whether the obligation was fulfilled.
		ObligationResponse response = this.getHandler().fillObligation(fill);
		
		//handle the response
		this.handleResponse(response);
		
		//set schedule the next obligation
		ScheduledObligation o = this.requestNextObligation(state);
		this.scheduleEvent(o, state);
		
	}
	
	public void scheduleEvent(ScheduledObligation o, SimState state) {
	
		// if next obligation is null, then do not schedule another event.
		if (o == null || o.getObligation() == null) {
			return;
		}

		//set the next obligation
		this.scheduleNextEvent(o.getObligation());
		
		state.schedule.scheduleOnce(o.getScheduledTime(), this);
	}
	
	public abstract ScheduledObligation requestNextObligation(SimState state);
	
	public void scheduleNextEvent(Obligation o) {
		this.nextObligation = o;
	}
}
