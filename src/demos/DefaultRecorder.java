package demos;

import agents.Agent;
import behaviours.DefaultException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DefaultRecorder {

    private static PrintWriter defaultsFile;

    public DefaultRecorder() {
        init();
    }

    private void init() {
        try {
            defaultsFile = new PrintWriter("defaultsFile.csv");
        } catch (FileNotFoundException e) {
            //todo: empty catch block
        }
    }


    public static void recordDefault(DefaultException defaultException) {
        defaultsFile.print(defaultException.getAgent().getName());
        defaultsFile.print(", " + defaultException.getTimestep());
        defaultsFile.print(", " + defaultException.getAgent().getLCR());
        defaultsFile.print(", " + defaultException.getAgent().getLeverage());




    }
}
