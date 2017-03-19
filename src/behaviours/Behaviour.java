package behaviours;

import actions.Action;
import actions.PayLoan;
import actions.PullFunding;
import actions.SellAsset;
import agents.Agent;
import demos.BoEDemo;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class Behaviour {
    private Agent me;
    private ArrayList<Action> availableActions;

    Behaviour (Agent me) {
        this.me = me;
    }

    protected abstract void chooseActions() throws DefaultException;

    public void act() {
        if (!(me.isAlive())) {
            System.out.println(me.getName() +
                    " cannot act. I'm crucified, dead and buried, and have descended into hell.");
            return;
        }

        System.out.println("\n"+me.getName()+" is acting at time "+ BoEDemo.getTime()+".\n");

        me.step();
        me.printBalanceSheet();
        me.printMailbox();

        availableActions = me.getAvailableActions(me);

        try {
            chooseActions();
        } catch (DefaultException e) {
            me.triggerDefault();
        }

        System.out.println(me.getName()+" done.\n*********");

    }

    private double maxLiabilitiesToPayOff() {
        return availableActions.stream()
                .filter(PayLoan.class::isInstance)
                .mapToDouble(Action::getMax).sum();
    }

    void payOffLiabilities(double amount) {
        System.out.println("Pay off liabilities (delever) proportionally: "+amount);

        if(amount > maxLiabilitiesToPayOff()) {
            amount = maxLiabilitiesToPayOff();
            System.out.println("We do not have enough liabilites to pay off this amount.\n" +
                    "We can only pay off "+ amount);
        }

        ArrayList<Action> payLoanActions = getAllActionsOfType(PayLoan.class);

        double totalLiabilitiesToPayOff = payLoanActions.stream()
                .mapToDouble(Action::getMax)
                .sum();

        for (Action action : payLoanActions) {
            action.setAmount(action.getMax() * amount / totalLiabilitiesToPayOff);
            action.perform();
        }
    }


    ArrayList<Action> getAllActionsOfType(Class<? extends Action> actionType) {
        return availableActions.stream()
                .filter(actionType::isInstance)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public double sellAssetsProportionally(double amount) {
        if (amount > 0) {
            System.out.println("Sell assets proportionally: " + amount);
            ArrayList<Action> sellAssetActions = getAllActionsOfType(SellAsset.class);

            double totalSellableAssets = sellAssetActions.stream()
                    .mapToDouble(Action::getMax)
                    .sum();

            if (!(totalSellableAssets > 0)) {
                System.out.println("We cannot sell assets.");
                return 0.0;
            }

            if (totalSellableAssets < amount) {
                System.out.println("We do not have enough assets to sell! We can at most sell "+totalSellableAssets);
                amount = totalSellableAssets;
            }

            for (Action action : sellAssetActions) {
                action.setAmount(action.getMax() * amount / totalSellableAssets);
                action.perform();
            }

            return amount;
        } else {
            return 0.0;
        }
    }

    public double pullFundingProportionally(double amount) {

        ArrayList<Action> pullFundingActions = getAllActionsOfType(PullFunding.class);

        double totalFundingThatCanBePulled = pullFundingActions.stream()
                .mapToDouble(Action::getMax)
                .sum();

        if (totalFundingThatCanBePulled < amount) {
            amount = totalFundingThatCanBePulled;
        }

        if (!(amount > 0)) return 0.0;


        for (Action action : pullFundingActions) {
            action.setAmount(action.getMax() * amount / totalFundingThatCanBePulled);
            action.perform();
        }

        return amount;
    }

    public double raiseLiquidityWithPeckingOrder(double amount) {
        if (!(amount>0)) return 0.0;
        double fundingPulled = 0.0;
        double firesales = 0.0;

        double totalFundingThatCanBePulled = getAllActionsOfType(PullFunding.class).stream()
                .mapToDouble(Action::getMax)
                .sum();

        double totalAssetsThatCanBeSold = getAllActionsOfType(SellAsset.class).stream()
                .mapToDouble(Action::getMax)
                .sum();

        if (!((totalAssetsThatCanBeSold + totalFundingThatCanBePulled)>0)) {
            System.out.println("We can't raise any liquidity.");
            return 0.0;
        } else if (amount > (totalFundingThatCanBePulled + totalAssetsThatCanBeSold)) {
            System.out.println("We can't raise this much liquidity. We will only raise "+
                    (totalAssetsThatCanBeSold+totalFundingThatCanBePulled));
            amount = totalAssetsThatCanBeSold + totalFundingThatCanBePulled;
        }

        System.out.println("I am raising "+amount+" liquidity, and can pull "+totalFundingThatCanBePulled);
        double amountToPullFunding = Math.min(totalFundingThatCanBePulled, amount);
        System.out.println("I'll try to pull "+amountToPullFunding);
        if (amountToPullFunding > 0) {
            fundingPulled = pullFundingProportionally(amountToPullFunding);
            System.out.println("I succeeded in pulling "+fundingPulled);
            amount -= fundingPulled;
            System.out.println("I still need to raise "+amount);
        }

        if (!(amount >0)) {
            return fundingPulled;
        }

        double assetsToSell = Math.min(amount, totalAssetsThatCanBeSold);
        if (assetsToSell>0) {
            firesales = sellAssetsProportionally(assetsToSell);
            amount -= firesales;
        }

        if (amount > 0) {
            System.out.println("We could not raise enough liquidity.");
        }

        System.out.println("We managed to put orders to raise "+(firesales+fundingPulled));
        System.out.println("\tfiresales: "+firesales+"\n\tpull funding: "+fundingPulled);
        return fundingPulled + firesales;
    }
}