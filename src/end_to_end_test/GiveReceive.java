package end_to_end_test;

import behaviours.Behaviour;
import economicsl.NotEnoughGoods;
import economicsl.Trade;


/**
 * Created by taghawi on 28/03/17.
 */
public class GiveReceive extends Trade {
    public GiveReceive(String name, double teddies, double money) {
        super(name);
        this.addCash(money);
        this.getMainLedger().addGoods("teddies", teddies, 100.0);

    }

    @Override
    public Behaviour getBehaviour() {
        return null;
    }


    public void give(GiveReceive receiver) {
        try {
            this.give(receiver, "ball", 1);
            System.out.println(" gave ball ");
        } catch (NotEnoughGoods notEnoughGoods) {

        }
    }

}

