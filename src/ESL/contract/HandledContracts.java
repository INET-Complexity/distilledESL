package ESL.contract;

import ESL.contract.handler.ContractHandler;
import ESL.contract.messages.FillObligation;
import ESL.contract.messages.ObligationResponse;
import ESL.inventory.Contract;

public abstract class HandledContracts extends Contract {

	private ContractHandler handler;
	
	public HandledContracts(String name, ContractHandler handler) {
		super(name);
		this.handler = handler;
	}

	public ContractHandler getHandler() {
		return handler;
	}

	public void setHandler(ContractHandler handler) {
		this.handler = handler;
	}
	
	public ObligationResponse sendObligation(FillObligation fill) {
		return handler.fillObligation(fill);
	}
	
	public abstract void handleResponse(ObligationResponse response);
	
	
}
