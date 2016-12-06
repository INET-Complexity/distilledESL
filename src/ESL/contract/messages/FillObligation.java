package ESL.contract.messages;

import ESL.contract.obligation.Obligation;

public class FillObligation {
	
	private Obligation o;
	
	public FillObligation(Obligation o) {
		this.o = o;
	}

	public Obligation getObligation() {
		return o;
	}

	public void setObligation(Obligation o) {
		this.o = o;
	}

}
