package demos;

import actions.Action;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;

public class ActionsRecorder {
    private PrintWriter actionsFile;

    public ActionsRecorder() {
        Locale.setDefault(Locale.UK);
        init();
    }

    private void init() {
        try {
            actionsFile = new PrintWriter("actions.csv");

            actionsFile.println("Simulation number, Timestep, StressAgent, Action, Amount");
        } catch (FileNotFoundException e) {
            //todo: empty catch block
        }
    }


    public void recordAction(Action action) {
        actionsFile.print(Integer.toString(Model.simulationNumber));
        actionsFile.print(", " + Integer.toString(Model.getTime()));
        actionsFile.print(", " + action.getAgent().getName());
        actionsFile.print(", " + action.getName());
        actionsFile.print(", " + action.getAmount());
        actionsFile.println();

    }

    public void finish() {
        actionsFile.close();
    }
}
