package agents;

import actions.*;
import behaviours.BankBehaviour;
import behaviours.Behaviour;
import contracts.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * This class represents a simple me with a single Ledger, called 'general Ledger'.
 *
 * Every Bank has a BankBehaviour.
 */
public class Bank extends Agent implements CanPledgeCollateral {

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
        //TODO: THIS SHOULD BE BEHAVIOUR
        // First, get a set of all my Assets that can be pledged as collateral
        HashSet<Contract> potentialCollateral = mainLedger.getAssetsOfType(AssetCollateral.class);

        double maxHaircutValue = getMaxUnencumberedHaircuttedCollateral();
        double pledgedSoFar = 0.0;

        for (Contract contract : potentialCollateral) {
            CanBeCollateral asset = (CanBeCollateral) contract;

            double quantityToPledge = total * asset.getUnencumberedValue() * (1.0 - asset.getHaircut()) / maxHaircutValue;
            repo.pledgeCollateral(asset, quantityToPledge);
            pledgedSoFar += quantityToPledge;

        }

        repo.pledgeCashCollateral(total - pledgedSoFar);
    }

    @Override
    public double getMaxUnencumberedHaircuttedCollateral() {
        return mainLedger.getAssetsOfType(AssetCollateral.class).stream()
                .mapToDouble(asset ->
                        ((CanBeCollateral) asset).getUnencumberedValue() *
                                (1.0 - ((CanBeCollateral) asset).getHaircut()))
                .sum() + getCash();
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

    public LCR_Constraint getLCR_constraint() {
        return lcr_constraint;
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
    }

    public double getRWAratio() {
        return rwa_constraint.getRWAratio();
    }

    @Override
    public void triggerDefault() {
        super.triggerDefault();

        HashSet<Contract> loansAndRepos = mainLedger.getLiabilitiesOfType(Loan.class);
        for (Contract loan : loansAndRepos) {
            ((Loan) loan).liquidate();
        }

        ArrayList<Action> pullFundingActions = getAvailableActions(this).stream()
                .filter(action -> action instanceof PullFunding)
                .collect(Collectors.toCollection(ArrayList::new));

        for (Action action : pullFundingActions) {
            action.setAmount(action.getMax());
            action.perform();
        }

    }
}
