package behaviours;

import agents.Bank;
import actions.Action;
import actions.PullFunding;
import actions.PayLoan;
import actions.SellAsset;

import java.util.ArrayList;

import static java.lang.Math.max;

public class BankBehaviour extends Behaviour {

    public Bank bank;

    public BankBehaviour(Bank bank) {
        super(bank);
        this.bank = bank;
    }

    @Override
    protected void chooseActions() {
        if (bank.getBankLeverageConstraint().isBelowBuffer()) {
            // If leverage is below buffer, we must de-lever

            double amountToDelever = bank.getBankLeverageConstraint().getAmountToDelever();
            double maxLiabilitiesToPayOff = maxLiabilitiesToPayOff();
            double payLoan = 0.0;

            System.out.println("\nAmount to delever is "+String.format("%.2f", amountToDelever));


            if (maxLiabilitiesToPayOff == 0) {
                System.out.println("Strange! No liabilities to pay off.");
                return;
            }

            if (maxLiabilitiesToPayOff < amountToDelever) {
                System.out.println("Strange! We do not have enough liabilites to fully de-lever. " +
                        "We will de-lever an amount "+maxLiabilitiesToPayOff);
                amountToDelever = maxLiabilitiesToPayOff;
            }


            // 1. Use cash to pay liabilities
            double maxCashToSpend = max(bank.getCash() - bank.getLCR_constraint().getCashTarget() , 0.0);

            if (maxCashToSpend==0) {
                System.out.println();
                System.out.println("We are at or below our LCR target. We cannot use cash to pay back liabilities.");
            } else {
                if (maxCashToSpend > amountToDelever) {
                    payLoan += amountToDelever;
                    System.out.println("We managed to de-lever by paying back liabilities => no contagion.");
                    payOffLiabilities(payLoan);
                    return;

                } else {
                    payLoan += maxCashToSpend;
                    amountToDelever -= maxCashToSpend;
                }
            }


            // 2. Use other actions according to the pecking order
            while (amountToDelever > 0.0 && actionsLeft()) {
                Action nextAction = findActionOfType(PullFunding.class);
                if (nextAction == null) {
                    nextAction = findActionOfType(SellAsset.class);
                    if (nextAction == null) {
                        // We can't find any more suitable actions. Stop looking.
                        break;
                    }
                }

                if (nextAction.getMax() > amountToDelever) {
                    nextAction.setAmount(amountToDelever);
                    addAction(nextAction);

                    payLoan += nextAction.getAmount();
                    amountToDelever = 0.0;

                    System.out.println("\nWe found a set of actions to reach our leverage target!");
                    break;

                } else {
                    nextAction.setAmount(nextAction.getMax());
                    addAction(nextAction);

                    payLoan += nextAction.getAmount();

                    amountToDelever -= nextAction.getMax();
                }
            }

            if (amountToDelever < 0.001) {
                payOffLiabilities(payLoan);
                return;
            }

            // 3. If we're still not done, break the LCR constraint and use up more cash
            double remainingCash = bank.getCash() - maxCashToSpend;
            if (remainingCash > amountToDelever) {
                System.out.println("We managed to de-lever by breaking the LCR constraint.");
                payLoan += amountToDelever;
                payOffLiabilities(payLoan);
            } else {
                System.out.println("We cannot reach the leverage target this round.");
                payLoan += remainingCash;
                payOffLiabilities(payLoan);
            }

        } else {
            System.out.println("Leverage is above buffer. No need to do anything!");
            //We're fine, do nothing
        }
    }




    public void triggerDefault() {
        ArrayList<Action> availableActions = bank.getAvailableActions(bank);
        for (Action action : availableActions ) {
            // Sell every asset!
            if (action instanceof SellAsset) {
                action.setAmount(action.getMax());
                action.perform();
            } else if (action instanceof PayLoan) {
                // Loans must be terminated and changed into assets.
                ((PayLoan)action).getLoan().liquidate();
            } // all other actions are ignored
        }

    }

}
