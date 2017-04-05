package demos;

import agents.*;
import contracts.Asset;
import contracts.AssetCollateral;
import contracts.AssetMarket;
import contracts.CanBeCollateral;
import economicsl.Agent;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class records all statistics.
 */
public class Recorder {
    private AssetMarket market;
    private PrintWriter marketFile;
    private PrintWriter banksFile;
    private PrintWriter lossesFile;
    private PrintWriter collateralFile;
    private ArrayList<Asset.AssetType> assetTypes;
    private StressAgent[] banks;
    private StressAgent[] allAgents;
    private double totalInitialEquity;

    private String marketHeader;

    public Recorder() {
        Locale.setDefault(Locale.UK);

        try {
            marketFile = new PrintWriter("marketFile.csv");
            banksFile = new PrintWriter("banks.csv");
            lossesFile = new PrintWriter("losses.csv");
            collateralFile = new PrintWriter("collateral.csv");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void startSimulation(HashMap<String, StressAgent> modelAgents, AssetMarket market) {
        allAgents = new StressAgent[5];

        String[] names = new String[] {"Bank 1", "Bank 2", "Bank 3", "Hedgefund 1", "AssetManager 1"};
        for (int i = 0; i < names.length; i++) {
            allAgents[i] = modelAgents.containsKey(names[i]) ? modelAgents.get(names[i]) : null;
        }

        banks = new StressAgent[3];

        System.arraycopy(allAgents, 0, banks, 0, 3);

        this.market = market;

        assetTypes = market.getAssetTypes();


        // MARKET ///////////////////////
        marketHeader = "Simulation number, Timestep";

        for (Asset.AssetType assetType : assetTypes) {
            marketHeader = marketHeader + ", price_" + assetType;
        }

        for (Asset.AssetType assetType : assetTypes) {
            marketHeader = marketHeader + ", haircut_" + assetType;
        }

        for (Asset.AssetType assetType : assetTypes) {
            marketHeader = marketHeader + ", totalAmountSold_" + assetType;
        }

        marketFile.println(marketHeader);


        // BANKS ///////////////////////

        String bankLine = "Simulation number, Timestep";
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getName()+"_leverage";
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getName()+"_RWA_ratio";
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getName()+"_LCR";
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getName()+"_equity";
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getName()+"_alive";
        }

        banksFile.println(bankLine);


        // LOSSES ///////////////////////

        String lossesLine = "Simulation number, Timestep";
        for (Agent agent : allAgents) {
            lossesLine = lossesLine + ", " +
                    ((agent != null) ? agent.getName()+"_loss" : "MISSING");
        }

        lossesLine = lossesLine + ", totalEquityLoss";

        lossesFile.println(lossesLine);

        totalInitialEquity = 0.0;
        for (StressAgent agent : allAgents) {
            if (agent != null) {
                totalInitialEquity += agent.getEquityValue();
            }
        }

        System.out.println("Total initial equity is :" + totalInitialEquity);


        // COLLATERAL ///////////////////////

        String collateralLine = "Simulation number, Timestep";
        for (Agent agent: allAgents) {
            for (Asset.AssetType assetType : assetTypes) {
                collateralLine = collateralLine + ", " + agent.getName() + "_" + assetType + "_total_quantity";
                collateralLine = collateralLine + ", " + agent.getName() + "_" + assetType + "_encumbered_quantity";
            }
        }
        collateralFile.println(collateralLine);

    }

    public void record() {

        // Write all market prices and haircuts
        String line = Integer.toString(Model.simulationNumber);

        line = line + ", " + Integer.toString(Model.getTime());

        for (Asset.AssetType assetType : assetTypes) {
            line = line + ", " + Double.toString(market.getPrice(assetType));
        }

        for (Asset.AssetType assetType : assetTypes) {
            line = line + ", " + Double.toString(market.getHaircut(assetType));
        }

        for (Asset.AssetType assetType : assetTypes) {
            line = line + ", " + Double.toString(market.getTotalAmountSold(assetType));
        }

        System.out.println("Market conditions: \n"+line);
        marketFile.println(line);


        String bankLine = Integer.toString(Model.simulationNumber);
        bankLine = bankLine +", "+ Integer.toString(Model.getTime());

        for (StressAgent agent : banks) {
            bankLine = bankLine + ", "+agent.getLeverage();
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+((Bank) agent).getRWAratio();
        }
        for (StressAgent agent : banks) {
            bankLine = bankLine + ", "+agent.getLCR();
        }
        for (StressAgent agent : banks) {
            bankLine = bankLine + ", "+agent.getEquityValue();
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.isAlive();
        }

        banksFile.println(bankLine);


        String lossesLine = Integer.toString(Model.simulationNumber);
        lossesLine = lossesLine +", "+ Integer.toString(Model.getTime());

        double totalEquity = 0.0;
        for (StressAgent agent : allAgents) {
            if (agent != null) {
                lossesLine = lossesLine + ", " + String.format("%.2f", (100.0*agent.getEquityLoss())) + "%";
                totalEquity += Math.max(0.0, agent.getEquityValue());
            } else {
                lossesLine = lossesLine + ", " + "MISSING";
            }
        }

        double totalEquityLoss = (totalEquity - totalInitialEquity) / totalInitialEquity;
        lossesLine = lossesLine + ", "+String.format("%.2f", totalEquityLoss)+"%";
        lossesFile.println(lossesLine);


        String collateralLine = Integer.toString(Model.simulationNumber);
        collateralLine = collateralLine + ", "+ Integer.toString(Model.getTime());
        for (Agent agent: allAgents) {
            for (Asset.AssetType assetType : assetTypes) {
                collateralLine = collateralLine + ", " + Double.toString(agent.getMainLedger()
                        .getAssetsOfType(AssetCollateral.class).stream()
                        .filter(asset -> ((Asset) asset).getAssetType()==assetType)
                        .mapToDouble(asset -> ((Asset) asset).getQuantity()).sum());
                collateralLine = collateralLine + ", " + Double.toString(agent.getMainLedger()
                        .getAssetsOfType(AssetCollateral.class).stream()
                        .filter(asset -> ((Asset) asset).getAssetType()==assetType)
                        .mapToDouble(asset -> ((CanBeCollateral) asset).getUnencumberedQuantity()).sum());            }
        }
        collateralFile.println(collateralLine);



        //todo: equity and assets and cash for banks and hF
        // todo: redemptions from asset manager.


        marketFile.flush();
        banksFile.flush();
        lossesFile.flush();
        collateralFile.flush();

    }


    public void finish() {

        marketFile.close();
        banksFile.close();
        lossesFile.close();
        collateralFile.close();
    }


}
