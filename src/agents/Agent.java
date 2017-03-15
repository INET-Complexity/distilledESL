package agents;

import accounting.Ledger;
import actions.Action;
import behaviours.Behaviour;
import contracts.Asset;
import contracts.Contract;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Agent {
    Ledger mainLedger;
    private String name;
    private HashSet<Request> requestInbox;
    private HashSet<Request> requestOutbox;


    public Agent(String name) {
        this.name = name;
        mainLedger = new Ledger(this);
        requestInbox = new HashSet<>();
        requestOutbox = new HashSet<>();
    }

    public void addToInbox(Request request) {
        requestInbox.add(request);
    }

    public void addToOutbox(Request request) {
        requestOutbox.add(request);
    }

    public double getTotalPullFunding() {
        return requestInbox.stream()
                .mapToDouble(Request::getAmount).sum();
    }

    public double getMaturedPayments() {
        return requestInbox.stream()
                .filter(Request::isDue)
                .mapToDouble(Request::getAmount).sum();
    }

    public double getPendingPayments() {
        return requestInbox.stream()
                .mapToDouble(Request::getAmount).sum();
    }

    public void fulfilAllRequests() {
        for (Request request : requestInbox) {
            request.fulfil();
            requestInbox.remove(request);
        }
    }

    public void fulfilMaturedRequests() {
        for (Request request : requestInbox) {
            if (request.isDue()) {
                request.fulfil();
                requestInbox.remove(request);
            }
        }
    }

    public void tick() {
        for (Request request : requestInbox) {
            request.tick();
        }
    }

    public String getName() {
        return name;
    }

    public void pullFunding(double amount, Contract loan) {
        mainLedger.pullFunding(amount, loan);
    }

    /**
     * Pre-condition: we have enough liquidity!
     *
     * @param amount the amount to pay back of this loan
     * @param loan   the loan we are paying back
     */
    public void payLoan(double amount, Contract loan) {
        mainLedger.payLiability(amount, loan);
        //Todo: throw an exception
    }

    public void sellAssetForValue(Asset asset, double value) {
        mainLedger.sellAsset(value, asset.getClass());
    }

    public void devalueAsset(Asset asset, double valueLost) {
        System.out.println(getName() + " made a loss of " + valueLost + " from the sale of an asset of type " + asset.getAssetType());
        mainLedger.devalueAsset(valueLost, asset);
    }

    public void add(Contract contract) {
        if (contract.getAssetParty() == this) {
            // This contract is an asset for me.
            mainLedger.addAsset(contract);
        } else if (contract.getLiabilityParty() == this) {
            // This contract is a liability for me
            mainLedger.addLiability(contract);
        }
    }

    public void addCash(double amount) {
        mainLedger.addCash(amount);
    }

    public double getCash() {
        return mainLedger.getCash();
    }


    public ArrayList<Action> getAvailableActions(Agent me) {
        return mainLedger.getAvailableActions(this);
    }

    public Ledger getMainLedger() {
        return mainLedger;
    }

    public abstract Behaviour getBehaviour(); // Make this abstract to force every implementation to provide a behaviour

    public void act() {
        getBehaviour().act();
    }

    public void updateAssetPrices() {
        mainLedger.updateAssetPrices();
    }

    public double getLeverage() {
        return (1.0 * getEquityValue() / getAssetValue());
    }

    public void liquidateLoan(double initialValue, double valueFraction, Contract loan) {
        mainLedger.liquidateLoan(initialValue, valueFraction, loan);
    }

    public double getAssetValue() {
        return mainLedger.getAssetValue();
    }

    public double getLiabilityValue() {
        return mainLedger.getLiabilityValue();
    }

    public double getEquityValue() {
        return mainLedger.getEquityValue();
    }


    public void printBalanceSheet() {
        System.out.println();
        System.out.println("Balance Sheet of " + getName());
        System.out.println("**************************");
        mainLedger.printBalanceSheet();
        System.out.println("Leverage ratio: " + String.format("%.2f", 100 * getLeverage()) + "%");
        System.out.println();
    }


}
