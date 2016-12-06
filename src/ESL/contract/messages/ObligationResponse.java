package ESL.contract.messages;

import ESL.contract.obligation.Obligation;

public class ObligationResponse {

	private Obligation obligation;
	private Boolean filled;
	
	public ObligationResponse(Obligation o, Boolean filled) {
		this.obligation = o;
		this.filled = filled;
	}

	public Obligation getObligation() {
		return obligation;
	}

	public void setObligation(Obligation obligation) {
		this.obligation = obligation;
	}

	public Boolean getFilled() {
		return filled;
	}

	public void setFilled(Boolean filled) {
		this.filled = filled;
	}
	
	
}
