package ESL.contract.obligation;

import ESL.contract.obligation.Obligation;

public class ScheduledObligation {

	private Obligation obligation;
	private Double scheduledTime;
	
	public ScheduledObligation(Obligation o, Double time) {
		this.obligation = o;
		this.scheduledTime = time;
	}

	public Obligation getObligation() {
		return obligation;
	}

	public void setObligation(Obligation obligation) {
		this.obligation = obligation;
	}

	public Double getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Double scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	
	
}
