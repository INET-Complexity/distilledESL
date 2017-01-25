package doubleEntryComponents.actions;

import doubleEntryComponents.Bank;

import java.util.ArrayList;

/**
 * This class implements a Bank's behaviour consisting of:
 *  1) Pull Funding
 *  2) Sell Assets
 *  3) Pay with cash
 */
public class BankBehaviour2 extends Behaviour {

    public BankBehaviour2(Bank bank) {
        super(bank);
    }

    public Action getNextAction(ArrayList<Action> availableActions) {

        Action cancelLoan = findCancelLoanAction(availableActions);
        if (cancelLoan != null) {
            return cancelLoan;
        }

        Action sellAsset = findSellAssetAction(availableActions);
        if (sellAsset != null) {
            return sellAsset;
        }

        Action payLoan = findPayLoanAction(availableActions);
        if (payLoan != null) {
            return payLoan;
        }

        System.out.println("No more actions found.");
        return null;
    }

}
