package demos;

import agents.Agent;
import agents.Bank;
import agents.CashProvider;
import agents.Investor;
import contracts.Asset;
import contracts.AssetMarket;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * This class records all statistics.
 */
public class Recorder {
    private HashSet<Agent> allAgents;
    private AssetMarket market;

    private PrintWriter marketFile;
    private PrintWriter banksFile;
    private PrintWriter lossesFile;
    private ArrayList<Asset.AssetType> assetTypes;
    private ArrayList<Agent> banks;


    private double totalInitialEquity;
    private HashMap<Agent, Double> initialEquity;

    private int timestep;
    private String marketHeader;

    public Recorder(HashSet<Agent> allAgents, AssetMarket market) {
        this.allAgents = new HashSet<Agent>();
        for (Agent agent : allAgents) {
            if (!(agent instanceof CashProvider) && !(agent instanceof Investor) ) {
                this.allAgents.add(agent);
            }
        }

        this.market = market;
        this.initialEquity = new HashMap<>();

    }

    // Things to record:
    // Market file:
    // Timestep, price asset 1, price asset 2, ... haircut asset 1, haircut asset 2, ...


    public void init() {
        timestep = 0;
//        allAgents.removeIf(agent -> agent instanceof CashProvider);
//        allAgents.removeIf(agent -> agent instanceof Investor);

        try {
            marketFile = new PrintWriter("marketFile.csv");
            banksFile = new PrintWriter("banks.csv");
            lossesFile = new PrintWriter("losses.csv");

            assetTypes = market.getAssetTypes();
            marketHeader = "Timestep";

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

            banks = allAgents.stream()
                    .filter(agent -> agent instanceof Bank)
                    .collect(Collectors.toCollection(ArrayList::new));


            String bankLine = "Timestep";
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

            String lossesLine = "Timestep";
            for (Agent agent : allAgents) {
                lossesLine = lossesLine + ", "+agent.getName()+"_loss";
            }

            lossesLine = lossesLine + ", totalEquityLoss";

            lossesFile.println(lossesLine);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        totalInitialEquity = 0.0;
        for (Agent agent : allAgents) {
            totalInitialEquity += agent.getEquityValue();
            initialEquity.put(agent, agent.getEquityValue());
        }

        System.out.println("Total initial equity is :"+ totalInitialEquity);

    }

    public void record() {
        timestep++;

        // Write all market prices and haircuts
        String line = Integer.toString(timestep);

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


        String bankLine = Integer.toString(timestep);
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getLeverage();
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+((Bank) agent).getRWAratio();
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+((Bank)agent).getLCR_constraint().getLCR();
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.getEquityValue();
        }
        for (Agent agent : banks) {
            bankLine = bankLine + ", "+agent.isAlive();
        }

        banksFile.println(bankLine);


        String lossesLine = Integer.toString(timestep);
        double totalEquity = 0.0;
        for (Agent agent : allAgents) {
            lossesLine = lossesLine + ", "+ String.format("%.2f", 100.0*(1.0 - agent.getEquityValue() / initialEquity.get(agent)))+"%";
            totalEquity += Math.max(0.0, agent.getEquityValue());
        }

        double totalEquityLoss = 100.0 * (1.0 - totalEquity / totalInitialEquity);
        lossesLine = lossesLine + ", "+String.format("%.2f", totalEquityLoss)+"%";
        lossesFile.println(lossesLine);




        //todo: equity and assets and cash for banks and hF
        // todo: redemptions from asset manager.


        marketFile.flush();
        banksFile.flush();
        lossesFile.flush();

    }


    public void finish() {

        marketFile.close();
        banksFile.close();
        lossesFile.close();
    }





}
