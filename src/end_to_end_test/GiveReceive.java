package usecases.MnlEconomicProblem;

import behaviours.Behaviour;
import economicsl.NotEnoughGoods;
import economicsl.Trade;

import static java.lang.Double.min;
import static jdk.nashorn.internal.objects.Global.print;

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

