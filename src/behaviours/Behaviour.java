package behaviours;

import actions.Action;

import java.util.ArrayList;

public abstract class Behaviour {
    public abstract void act();

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