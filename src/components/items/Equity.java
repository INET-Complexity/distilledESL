package components.items;

import ESL.inventory.Good;

public class Equity extends Good  implements Collateral {
    public Equity(Double amount) {
        super("Equity",amount);
    }

    //TODO: PRICE OF EQUITY?
    static GBP price;

    @Override
    public void setEncumbered() {
        if (this.encumbered) {
            System.out.println("Strange: I'm setting this as encumbered but it already is.");
        }
        this.encumbered=true;
    }

    @Override
    public void setUnencumbered() {
        this.encumbered=false;
    }

    @Override
    public boolean isEncumbered() {
        return this.encumbered;
    }

    private boolean encumbered;
}

