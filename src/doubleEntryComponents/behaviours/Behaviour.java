package doubleEntryComponents.behaviours;

import doubleEntryComponents.actions.Action;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class Behaviour {
    public abstract void act();

//    protected abstract ArrayList<Action> chooseActions(ArrayList<Action> availableActions);

    void performActions(ArrayList<Action> chosenActions) {
        if (chosenActions==null) return;
        for (Action action : chosenActions) {
            action.print();
            action.perform();
        }
    }

    void performAction(Action action) {
        if (action==null) return;
        action.print();
        action.perform();
    }

}