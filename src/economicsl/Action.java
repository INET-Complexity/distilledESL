package actions;

import economicsl.Agent;


import java.util.ArrayList;

public abstract class Action {
    private Agent me;

    public Action(Agent me) {
        this.me = me;
    }

    public void perform() {
        System.out.println("Model.actionsRecorder.recordAction(this); not called because deleted");
    }

    private double amount;

    public abstract double getMax();
    public abstract String getName();

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public static void print(ArrayList<Action> actions) {
        int counter = 1;
        for (Action action : actions) {
            System.out.println("Action "+counter+" -> "+action.getName());
            counter++;
        }
    }

    public abstract void print();

    public Agent getAgent() {return me;}
}
