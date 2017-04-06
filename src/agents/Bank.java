package agents;

import actions.*;
import behaviours.BankBehaviour;
import behaviours.Behaviour;
import contracts.*;
import economicsl.Contract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * This class represents a simple me with a single Ledger, called 'general Ledger'.
 *
 * Every Bank has a BankBehaviour.
 */
public class Bank extends StressAgent implements CanPledgeCollateral {

    private BankLeverageConstraint bankLeverageConstraint;
    private LCR_Constraint lcr_constraint;
    private BankBehaviour behaviour;
    private RWA_Constraint rwa_constraint;

    public Bank(String name) {
        super(name);
        this.bankLeverageConstraint = new BankLeverageConstraint(this);
        this.lcr_constraint = new LCR_Constraint(this);
        this.behaviour = new BankBehaviour(this);
        this.rwa_constraint = new RWA_Constraint(this);
    }

    @Override
    public void putMoreCollateral(double total, Repo repo) {
        // First, get a set of all my Assets that can be pledged as collateral
        HashSet<Contract> potentialCollateral = getMainLedger().getAssetsOfType(AssetCollateral.class);

        double maxHaircutValue = getMaxUnencumberedHaircuttedCollateral();
        double haircuttedValuePledgedSoFar = 0.0;

        for (Contract contract : potentialCollateral) {
            CanBeCollateral asset = (CanBeCollateral) contract;

            double quantityToPledge = asset.getUnencumberedQuantity() * total / maxHaircutValue;
            repo.pledgeCollateral(asset, quantityToPledge);
            haircuttedValuePledgedSoFar += quantityToPledge * asset.getPrice() * (1.0 - asset.getHaircut());

        }

        repo.pledgeCashCollateral(total - haircuttedValuePledgedSoFar);
    }

    @Override
    public double getMaxUnencumberedHaircuttedCollateral() {
        return getMainLedger().getAssetsOfType(AssetCollateral.class).stream()
                .mapToDouble(asset ->
                        ((CanBeCollateral) asset).getUnencumberedValue() *
                                (1.0 - ((CanBeCollateral) asset).getHaircut()))
                .sum() + getCash_() - getEncumberedCash();
    }

    public void withdrawCollateral(double excessValue, Repo repo) {
        repo.unpledgeProportionally(excessValue);
    }


    public void setBankLeverageConstraint(BankLeverageConstraint bankLeverageConstraint) {
        this.bankLeverageConstraint = bankLeverageConstraint;
    }

    public BankLeverageConstraint getBankLeverageConstraint() {
        return bankLeverageConstraint;
    }

    public double getCashBuffer() {return lcr_constraint.getCashBuffer();}
    public double getCashTarget() {return lcr_constraint.getCashTarget();}

    @Override
    public double getLCR() {
        return isAlive()? lcr_constraint.getLCR() : getLcrAtDefault();
    }

    public void setLCR_constraint(LCR_Constraint lcr_constraint) {
        this.lcr_constraint = lcr_constraint;
    }

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public void printBalanceSheet() {
        super.printBalanceSheet();
        System.out.println("Risk Weighted Asset ratio: "+String.format("%.2f", rwa_constraint.getRWAratio()*100.0) + "%");
        System.out.println("LCR is: "+String.format("%.2f", lcr_constraint.getLCR()*100) + "%");
    }

    public double getRWAratio() {
        return rwa_constraint.getRWAratio();
    }

    @Override
    public void triggerDefault() {
        super.triggerDefault();

        System.out.println("First, liquidate all loans (in the liability side).");
        HashSet<Contract> loansAndRepos = getMainLedger().getLiabilitiesOfType(Loan.class);
        for (Contract loan : loansAndRepos) {
            ((Loan) loan).liquidate();
        }

        System.out.println(getAvailableActions(this));

        ArrayList<Action> pullFundingActions = getAvailableActions(this).stream()
                .filter(action -> action instanceof PullFunding)
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.println(pullFundingActions);
        for (Action action : pullFundingActions) {
            action.setAmount(action.getMax());
            if (action.getAmount() > 0) action.perform();
        }

    }

    public void revalueAllLoans() {
        getMainLedger().getAssetsOfType(Loan.class).forEach(loan -> ((Loan) loan).reValueLoan());
    }
}
