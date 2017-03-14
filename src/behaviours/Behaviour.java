package behaviours;

import actions.Action;
import actions.PayLoan;
import agents.Agent;
import contracts.Loan;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class Behaviour {
    private Agent me;
    private ArrayList<Action> availableActions;
    private ArrayList<Action> chosenActions;

    Behaviour (Agent me) {
        this.me = me;
    }

    private void performActions(ArrayList<Action> chosenActions) {
        if (chosenActions==null) return;
        for (Action action : chosenActions) {
            action.print();
            action.perform();
        }
    }

    private void performAction(Action action) {
        if (action==null) return;
        action.print();
        action.perform();
    }

    protected abstract void chooseActions();

    public void act() {
        System.out.println(me.getName()+" is acting.");
        availableActions = me.getAvailableActions(me);
        System.out.println();
        System.out.println("My available actions are: ");
        Action.print(availableActions);

        chosenActions = new ArrayList<>();

        chooseActions();
        performActions(chosenActions);

        // Todo: tick here!
        me.tick();

    }

    double maxLiabilitiesToPayOff() {
        return availableActions.stream()
                .filter(PayLoan.class::isInstance)
                .mapToDouble(Action::getMax).sum();
    }

    // Should be called only once in the end!
    void payOffLiabilities(double amount) {
        PayLoan payLoan = (PayLoan) findActionOfType(PayLoan.class);

        if (amount > payLoan.getMax()) {
            payLoan.setAmount(payLoan.getMax());
            chosenActions.add(payLoan);
            payOffLiabilities(amount - payLoan.getMax());
        } else {
            payLoan.setAmount(amount);
            chosenActions.add(payLoan);
        }
    }

    boolean actionsLeft() {
        return !availableActions.isEmpty();
    }

    void addAction(Action action) {
       chosenActions.add(action);
       availableActions.remove(action);
    }

    /**
     * @param actionType the subclass of Action that we should be looking for
     * @return the first action in the list 'availableActions' that is of type
     * actionType.
     */
    Action findActionOfType(Class<? extends Action> actionType) {
        for (Action action : availableActions) {
            if (actionType.isInstance(action)) {
                return action;
            }
        }
        return null;
    }

    ArrayList<Action> getAllActionsOfType(Class<? extends Action> actionType) {
        return availableActions.stream()
                .filter(actionType::isInstance)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}