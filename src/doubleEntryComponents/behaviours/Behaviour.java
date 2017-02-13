package doubleEntryComponents.behaviours;

import doubleEntryComponents.actions.Action;

import java.util.ArrayList;

public abstract class Behaviour {
    public abstract void act();

    protected abstract ArrayList<Action> chooseActions(ArrayList<Action> availableActions);

    protected abstract void performActions(ArrayList<Action> chosenActions);
}