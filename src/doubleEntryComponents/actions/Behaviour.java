package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;

import java.util.ArrayList;

public abstract class Behaviour {

    private Bank bank;
    Behaviour(Bank bank) {
        this.bank = bank;
    }

    public void act() {
        ArrayList<Action> availableActions = bank.getAvailableActions(bank);
        ArrayList<Action> chosenActions = chooseActions(availableActions);
        performActions(chosenActions);

    }

    private ArrayList<Action> chooseActions(ArrayList<Action> availableActions) {
        if (bank.getLeverageConstraint().isBelowMin()) {
            ArrayList<Action> chosenActions = new ArrayList<>();
            chosenActions.add(new TriggerDefault());
            return chosenActions;
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

    private void performActions(ArrayList<Action> chosenActions) {
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
}
