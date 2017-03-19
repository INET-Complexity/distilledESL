package behaviours;

import actions.Action;
import actions.PayLoan;
import actions.PullFunding;
import actions.SellAsset;
import agents.Agent;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.NativeMath.min;

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

        System.out.println("\n"+me.getName()+" is acting.\n");

        me.step();
        me.printBalanceSheet();

        availableActions = me.getAvailableActions(me);
        System.out.println("\nMy available actions are: ");
        Action.print(availableActions);

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

    public void sellAssetsProportionally(double amount) {
        System.out.println("Sell assets proportionally: "+amount);
        ArrayList<Action> sellAssetActions = getAllActionsOfType(SellAsset.class);

        double totalSellableAssets = sellAssetActions.stream()
                .mapToDouble(Action::getMax)
                .sum();

        for (Action action : sellAssetActions) {
            action.setAmount(action.getMax() * amount / totalSellableAssets);
            action.perform();
        }
    }

    public void pullFundingProportionally(double amount) {
        System.out.println("Pull funding proportionally: "+amount);
        ArrayList<Action> pullFundingActions = getAllActionsOfType(PullFunding.class);

        double totalFundingThatCanBePulled = pullFundingActions.stream()
                .mapToDouble(Action::getMax)
                .sum();

        for (Action action : pullFundingActions) {
            action.setAmount(action.getMax() * amount / totalFundingThatCanBePulled);
            action.perform();
        }
    }

    public void raiseLiquidityWithPeckingOrder(double amount) {
        System.out.println("Raising liquidity: "+amount);
        double totalFundingThatCanBePulled = getAllActionsOfType(PullFunding.class).stream()
                .mapToDouble(Action::getMax)
                .sum();

        double amountToPullFunding = min(totalFundingThatCanBePulled, amount);
        if (amountToPullFunding > 0) pullFundingProportionally(amountToPullFunding);

        if (totalFundingThatCanBePulled < amount) {
            amount -= amountToPullFunding;
            double totalAssetsThatCanBeSold = getAllActionsOfType(SellAsset.class).stream()
                    .mapToDouble(Action::getMax)
                    .sum();
            double assetsToSell = min (amount, totalAssetsThatCanBeSold);
            if (assetsToSell >0) sellAssetsProportionally(assetsToSell);

            if (totalAssetsThatCanBeSold < amount) {
                System.out.println("we could not raise enough liquidity.");
            }
        }

    }
}