package doubleEntryComponents.behaviours;

import doubleEntryComponents.Bank;
import doubleEntryComponents.actions.Action;

import java.util.ArrayList;

/**
 * This class implements a Bank's behaviour consisting of:
 *  1) Sell Assets
 *  2) Pay with cash
 *  3) Pull Funding
 */
public class BankBehaviour3 extends BankBehaviour {

    public BankBehaviour3(Bank bank) {
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
