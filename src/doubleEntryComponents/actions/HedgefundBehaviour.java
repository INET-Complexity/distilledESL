package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;

import java.util.ArrayList;

/**
 * This class implements a Hedgefund's behaviour, which consists in:
 *  Perform all actions proportionally to initial holdings
 */
public class HedgefundBehaviour extends Behaviour {

    public HedgefundBehaviour(Bank bank) {
        super(bank);
    }

    @Override
    public Action getNextAction(ArrayList<Action> availableActions) {
        return null;
    }
}
