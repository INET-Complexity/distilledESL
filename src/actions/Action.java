package actions;

import java.util.ArrayList;

public abstract class Action {
    public abstract void perform();

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
}
