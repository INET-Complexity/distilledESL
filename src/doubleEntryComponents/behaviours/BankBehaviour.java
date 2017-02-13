package doubleEntryComponents.behaviours;

import doubleEntryComponents.Bank;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.actions.CancelLoan;
import doubleEntryComponents.actions.PayLoan;
import doubleEntryComponents.actions.SellAsset;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class BankBehaviour extends Behaviour {

    public Bank bank;
    public BankBehaviour(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void act() {
        System.out.println(bank.getName()+" is acting.");
        ArrayList<Action> availableActions = bank.getAvailableActions(bank);
        System.out.println("my available actions are: "+availableActions);
        ArrayList<Action> chosenActions = chooseActions(availableActions);
        performActions(chosenActions);

    }


    @Override
    @Nullable
    protected ArrayList<Action> chooseActions(ArrayList<Action> availableActions) {
        if (bank.getLeverageConstraint().isBelowMin()) {
            triggerDefault();
            return null;
        } else if (bank.getLeverageConstraint().isBelowBuffer()) {
            ArrayList<Action> chosenActions = new ArrayList<>();

            double amountToDelever = bank.getLeverageConstraint().getAmountToDelever();
            System.out.println("Amount to delever is "+amountToDelever);
            assert(amountToDelever > 0);

            Action nextAction = getNextAction(availableActions);

            while(nextAction != null) {

                if (nextAction.getMax() > amountToDelever) {
                    nextAction.setAmount(amountToDelever);
                    chosenActions.add(nextAction);
                    System.out.println("We found a set of actions to reach our leverage target!");
                    return chosenActions;
                } else {
                    nextAction.setAmount(nextAction.getMax());
                    amountToDelever -= nextAction.getMax();
                    chosenActions.add(nextAction);
                    availableActions.remove(nextAction);

                    nextAction=getNextAction(availableActions);
                }
            }

            // We cannot do anything else!
            System.out.println("We cannot reach the target leverage this round.");
            return chosenActions;

        } else {
            System.out.println("Leverage is above buffer. No need to do anything!");
            return null; //We're fine, do nothing
        }
    }

    public abstract Action getNextAction(ArrayList<Action> availableActions);

    @Override
    protected void performActions(ArrayList<Action> chosenActions) {
        if (chosenActions==null) return;
        for (Action action : chosenActions) {
                action.print();
                action.perform();
        }
    }

    Action findPayLoanAction(ArrayList<Action> availableActions) {
        for (Action action : availableActions) {
            if (action instanceof PayLoan) {
                return action;
            }
        }
        return null;
    }

    Action findSellAssetAction(ArrayList<Action> availableActions) {
        for (Action action : availableActions) {
            if (action instanceof SellAsset) {
                return action;
            }
        }
        return null;
    }

    Action findCancelLoanAction(ArrayList<Action> availableActions) {
        for (Action action : availableActions) {
            if (action instanceof CancelLoan) {
                return action;
            }
        }
        return null;
    }

    public void triggerDefault() {
        ArrayList<Action> availableActions = bank.getAvailableActions(bank);
        for (Action action : availableActions ) {
            // Sell every asset!
            if (action instanceof SellAsset) {
                action.setAmount(action.getMax());
                action.perform();
            } else if (action instanceof PayLoan) {
                // Loans must be terminated and changed into assets.
                ((PayLoan)action).getLoan().liquidate();
            } // all other actions are ignored
        }

    }
}
