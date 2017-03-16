package demos;

import agents.Agent;
import contracts.Asset;
import contracts.AssetMarket;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class records all statistics.
 */
public class Recorder {
    private HashSet<Agent> allAgents;
    private AssetMarket market;

    private PrintWriter marketFile;
    private PrintWriter statisticsFile;
    private ArrayList<Asset.AssetType> assetTypes;

    private int timestep;

    public Recorder(HashSet<Agent> allAgents, AssetMarket market) {
        this.allAgents = allAgents;
        this.market = market;
    }

    // Things to record:
    // Market file:
    // Timestep, price asset 1, price asset 2, ... haircut asset 1, haircut asset 2, ...


    public void init() {
        timestep = 0;

        try {
            marketFile = new PrintWriter("marketFile.csv");
            statisticsFile = new PrintWriter("statisticsFile.csv");

            assetTypes = market.getAssetTypes();
            String header = "Timestep";

            for (Asset.AssetType assetType : assetTypes) {
                header = header + ", price_" + assetType ;
            }

            for (Asset.AssetType assetType : assetTypes) {
                header = header + ", haircut_" + assetType ;
            }

            System.out.println(header);
            marketFile.println(header);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void record() {
        timestep++;

        // Write all market prices and haircuts
        String line = Integer.toString(timestep);

        for (Asset.AssetType assetType : assetTypes) {
            line = line + ", " + Double.toString(market.getPrice(assetType)) ;
        }

        for (Asset.AssetType assetType : assetTypes) {
            line = line + ", " + Double.toString(market.getHaircut(assetType)) ;
        }

        System.out.println(line);
        marketFile.println(line);

    }


    public void finish() {
        marketFile.close();
    }





}
