package demos;

import behaviours.DefaultException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;

public class DefaultRecorder {

    private PrintWriter defaultsFile;

    public DefaultRecorder() {
        Locale.setDefault(Locale.UK);
        init();
    }

    private void init() {
        try {
            defaultsFile = new PrintWriter("defaultsFile.csv");

            defaultsFile.println("Simulation number, Agent, timestep_of_default, type_of_default, equity_at_default, lcr_at_default, leverage_at_default");
        } catch (FileNotFoundException e) {
            //todo: empty catch block
        }
    }


    public void recordDefault(DefaultException defaultException) {
        defaultsFile.print(Integer.toString(Model.simulationNumber));
        defaultsFile.print(", " + defaultException.getAgent().getName());
        defaultsFile.print(", " + defaultException.getTimestep());
        defaultsFile.print(", " + defaultException.getTypeOfDefault());
        defaultsFile.print(", " + defaultException.getAgent().getEquityValue());
        defaultsFile.print(", " + defaultException.getAgent().getLCR());
        defaultsFile.print(", " + defaultException.getAgent().getLeverage());
        defaultsFile.println();

    }

    public void finish() {
        defaultsFile.close();
    }
}
