package ESL.contract.handler;


import ESL.contract.messages.FillObligation;
import ESL.contract.messages.ObligationResponse;
import ESL.contract.obligation.Obligation;

public abstract class ContractHandler {
	
	public final ObligationResponse fillObligation(FillObligation fill) {
	
		Obligation o = fill.getObligation();
		return handleObligation(o);
		
	}
	
	public abstract ObligationResponse handleObligation(Obligation o);
}
