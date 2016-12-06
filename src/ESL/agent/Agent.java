package ESL.agent;

import ESL.inventory.Inventory;

public class Agent {

	private Inventory inventory;
	private String name;
	
	public Agent() {
		this("");
	}
	
	public Agent(String name) {
		inventory = new Inventory();
		this.name = name;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
