package demos;

import agents.Agent;
import behaviours.DefaultException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;

public class RedemptionsRecorder {

    private PrintWriter redemptionsFile;

    public RedemptionsRecorder() {
        Locale.setDefault(Locale.UK);
        init();
    }

    private void init() {
        try {
            redemptionsFile = new PrintWriter("redemptions.csv");

            redemptionsFile.println("Simulation number, Timestep, Investor, AssetManager, nSharesRedeemed");
        } catch (FileNotFoundException e) {
            //todo: empty catch block
        }
    }


    public void recordRedemption(Agent investor, Agent assetManager, int nShares) {
        redemptionsFile.print(Integer.toString(Model.simulationNumber));
        redemptionsFile.print(", " + Integer.toString(Model.getTime()));
        redemptionsFile.print(", " + investor.getName());
        redemptionsFile.print(", " + assetManager.getName());
        redemptionsFile.print(", " + Integer.toString(nShares));
        redemptionsFile.println();

    }

    public void finish() {
        redemptionsFile.close();
    }
}
