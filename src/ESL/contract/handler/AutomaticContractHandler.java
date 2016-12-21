package ESL.contract.handler;

import ESL.agent.Agent;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;
import ESL.inventory.Good;
import ESL.inventory.Inventory;

public class AutomaticContractHandler extends ContractHandler{

	@Override
	public ObligationResponse handleObligation(Obligation o) {

		if (o == null) {
			return new ObligationResponse(null, false);
		}
		if (o.getWhat() instanceof Good) {
			return new ObligationResponse(o, handleGood(o));
		} else {
			// todo: what if the obligation is to move a contract?
			return new ObligationResponse(o, false);
		}
	}
	
	private Boolean handleGood(Obligation o) {
		
		Good g = (Good) o.getWhat();
		
		// attempt to pull a quantity of a good from a balancesheet
		try{
			o.getFrom().getInventory().remove(g);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		// give the good to a balancesheet
		o.getTo().getInventory().add(g);

		return true;
		
	}
}
