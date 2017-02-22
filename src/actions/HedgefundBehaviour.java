package actions;

import agents.Bank;
import behaviours.BankBehaviour;

import java.util.ArrayList;

/**
 * This class implements a Hedgefund's behaviour, which consists in:
 *  Perform all actions proportionally to initial holdings
 */
public class HedgefundBehaviour extends BankBehaviour {

    public HedgefundBehaviour(Bank bank) {
        super(bank);
    }

    @Override
    public Action getNextAction(ArrayList<Action> availableActions) {
        return null;
    }

    @Override
    protected ArrayList<Action> chooseActions(ArrayList<Action> availableActions) {

        Double amountToDelever = bank.getLeverageConstraint().getAmountToDelever();
        ArrayList<Action> chosenActions = new ArrayList<>();
        double totalInitialHoldings = bank.getMainBook().getAssetValue();

        for (Action action : availableActions) {
            assert((action instanceof PayLoan) || (action instanceof SellAsset));
            action.setAmount(1.0*action.getMax()*amountToDelever/totalInitialHoldings);
            chosenActions.add(action);
        }

        return chosenActions;

    }

}


