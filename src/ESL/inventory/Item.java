package ESL.inventory;

import components.items.Collateral;

public class Item {

	 private String name;

	    public Item (String name) {
	        this.name = name;
	    }
	    	    
	    public String getName() {
	    	return this.name;
	    }


	public void setCollateralType(Collateral collateralType) {
		this.collateralType = collateralType;
	}

	public void pledge() {
		try{
			collateralType.pledge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pledge(double amount) {
		try {
			collateralType.pledge(amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unpledge() {
		try{
			collateralType.unpledge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unpledge(double amount) {
		try{
			collateralType.unpledge(amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void isEncumbered() {
		collateralType.isEncumbered();
	}

	private Collateral collateralType;
}
