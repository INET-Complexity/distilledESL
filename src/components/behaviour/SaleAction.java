package components.behaviour;

import ESL.inventory.Good;
import components.markets.Market;

public class SaleAction implements Action {
    public void perform(double amount, Good good, Market market) {
        if (!market.canBeSoldHere(good)) {
            System.out.println("Strange! I'm trying to sell a good at the wrong market");
        } else {

        }
    }

}
