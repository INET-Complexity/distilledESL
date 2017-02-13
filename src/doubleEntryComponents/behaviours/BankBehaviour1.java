package doubleEntryComponents.behaviours;

import doubleEntryComponents.Bank;
import doubleEntryComponents.actions.Action;

import java.util.ArrayList;

/**
 * This class implements a Bank's behaviour consisting of:
 *
 *  1) Pay with cash
 *  2) Pull Funding
 *  3) Sell Assets
 */
public class BankBehaviour1 extends BankBehaviour {

    public BankBehaviour1(Bank bank) {
        super(bank);
    }

    @Override
    public Action getNextAction(ArrayList<Action> availableActions) {

        Action payLoan = findPayLoanAction(availableActions);
        if (payLoan != null) {
            return payLoan;
        }

        Action cancelLoan = findCancelLoanAction(availableActions);
        if (cancelLoan != null) {
            return cancelLoan;
        }

        Action sellAsset = findSellAssetAction(availableActions);
        if (sellAsset != null) {
            return sellAsset;
        }

        System.out.println("No more actions found.");
        return null;
    }

}
