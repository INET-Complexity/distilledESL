package end_to_end_test;

import org.economicsl.NotEnoughGoods;
import org.economicsl.Simulation;
import org.economicsl.Trade;


/**
 * Created by taghawi on 28/03/17.
 */
public class GiveAgent extends Trade {
    public GiveAgent(String name, double teddies, double money, Simulation simulation) {
        super(name, simulation);
        this.addCash(money);
        this.getMainLedger().addGoods("teddies", teddies, 100.0);

    }

    public void give(GiveAgent receiver) {
        try {
            this.give(receiver, "ball", 1);
            System.out.println(" gave ball ");
        } catch (NotEnoughGoods notEnoughGoods) {

        }
    }

}

