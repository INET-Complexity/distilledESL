package ESL.contract.obligation;

import ESL.agent.Agent;
import ESL.inventory.Item;

public class Obligation {

	private Agent to;
	private Agent from;
	private Item what;
	
	public Obligation(Agent to, Agent from, Item what) {
		this.to = to;
		this.from = from;
		this.what = what;
	}

	public Agent getTo() {
		return to;
	}

	public Agent getFrom() {
		return from;
	}

	public Item getWhat() {
		return what;
	}
	
	
}
