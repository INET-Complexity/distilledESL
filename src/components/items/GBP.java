package components.items;

import ESL.inventory.Contract;
import ESL.inventory.Good;
import components.behaviour.HasBehaviour;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Cash in GBP, dealt with as a good.
 *
 * @author rafa
 */
public class GBP extends Good implements Cash {

    public GBP(double amount) {
        super("GBP", amount);

    }

}

