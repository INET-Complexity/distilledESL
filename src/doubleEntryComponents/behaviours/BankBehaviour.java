package doubleEntryComponents.behaviours;

import doubleEntryComponents.Bank;
import doubleEntryComponents.actions.Action;
import doubleEntryComponents.actions.PullFunding;
import doubleEntryComponents.actions.PayLoan;
import doubleEntryComponents.actions.SellAsset;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.StrictMath.min;

public class BankBehaviour extends Behaviour {

    public Bank bank;
    public BankBehaviour(Bank bank) {
        this.bank = bank;
    }

    private ArrayList<Action> chosenActions;
    private ArrayList<Action> availableActions;

    @Override
    public void act() {
        System.out.println(bank.getName()+" is acting.");
        availableActions = bank.getAvailableActions(bank);
        System.out.println("my available actions are: "+availableActions);
        ArrayList<Action> chosenActions = chooseActions(availableActions);
        performActions(chosenActions);

    }

    @Nullable
    protected ArrayList<Action> chooseActions(ArrayList<Action> availableActions) {
        ArrayList<Action> chosenActions = new ArrayList<>();

        if (bank.getLeverageConstraint().isBelowBuffer()) {
            // If leverage is below buffer, we must de-lever

            double amountToDelever = bank.getLeverageConstraint().getAmountToDelever();
            System.out.println("Amount to delever is "+amountToDelever);
            assert(amountToDelever > 0);

            // First, we pay back the loan with as much cash as possible without breaking LCR constraint
            Action payLoan = findActionOfType(PayLoan.class, availableActions);

            if (payLoan!=null) {
                double maxCashToSpend = max(bank.getCash() - bank.getLCR_constraint().getCashTarget() , 0.0);

                if (maxCashToSpend==0) {
                    System.out.println("We are at or below our LCR target. We cannot use cash to pay back liabilities.");
                } else {
                    double maxAmount = min(maxCashToSpend, payLoan.getMax());

                    if (maxAmount > amountToDelever) {
                        payLoan.setAmount(amountToDelever);
                        chosenActions.add(payLoan);
                        System.out.println("We managed to de-lever by paying back liabilities => no contagion.");
                        return chosenActions;

                    } else {
                        payLoan.setAmount(maxAmount);
                        chosenActions.add(payLoan);

                        amountToDelever -= maxAmount;
                        availableActions.remove(payLoan);
                    }
                }
            }

            // Second, since we could not de-lever just by using cash, we try to choose other actions.
            while (amountToDelever > 0.0 && !availableActions.isEmpty()) {
                Action nextAction = findActionOfType(PullFunding.class, availableActions);
                if (nextAction == null) {
                    nextAction = findActionOfType(SellAsset.class, availableActions);
                    if (nextAction == null) {
                        // We can't find any more suitable actions. Stop looking.
                        break;
                    }
                }

                // Now, we must still be able to pay back liabilities.
                payLoan = findActionOfType(PayLoan.class, availableActions);
                if (payLoan==null) break;

                if (nextAction.getMax() > amountToDelever) {
                    nextAction.setAmount(amountToDelever);
                    chosenActions.add(nextAction);

                    payLoan.setAmount(nextAction.getAmount());
                    chosenActions.add(payLoan);

                    System.out.println("We found a set of actions to reach our leverage target!");
                    return chosenActions;
                } else {
                    nextAction.setAmount(nextAction.getMax());
                    chosenActions.add(nextAction);

                    payLoan.setAmount(nextAction.getAmount());
                    chosenActions.add(payLoan);

                    amountToDelever -= nextAction.getMax();
                    availableActions.remove(nextAction);
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

    public Action getNextAction(ArrayList<Action> availableActions) {
        return null;
    }

    /**
     * @param actionType the subclass of Action that we should be looking for
     * @param availableActions the list of actions in which to look
     * @return the first action in the list 'availableActions' that is of type
     * actionType.
     */
    Action findActionOfType(Class<? extends Action> actionType, ArrayList<Action> availableActions) {
        for (Action action : availableActions) {
            if (actionType.isInstance(action)) {
                return action;
            }
        }
        return null;
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
            if (action instanceof PullFunding) {
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
