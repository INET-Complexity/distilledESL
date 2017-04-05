package economicsl;

import agents.StressAgent;
import behaviours.Behaviour;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by taghawi on 30/03/17.
 */
public class Trade extends StressAgent {
    public Trade(String name) {
        super(name);
    }

    @Override
    public Behaviour getBehaviour() {
        return null;
    }

    /** Davoud
     * Trade good one against good two
     */
    public void barter(Agent trade_partner, String name_get, double amount_get, double value_get, String name_give, double amount_give, double value_give) throws NotEnoughGoods {
        throw new NotImplementedException();
    }

    public void give(Agent recipient, String good_name, double amount_give) throws NotEnoughGoods {

        double value = getMainLedger().getPhysicalThingValue(good_name);
        getMainLedger().subtractGoods(good_name, amount_give);
        GoodMessage good_message = new GoodMessage(good_name, amount_give, value);
        recipient.receiveGoodMessage(good_message);
    }
}
