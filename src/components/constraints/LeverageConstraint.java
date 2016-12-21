package components.constraints;

public class LeverageConstraint {

    public LeverageConstraint(HasLeverageConstraint agent) {
        this.agent = agent;
    }

    public double getCurrentLeverage() {
        return (agent.getAssetValue() - agent.getLiabilityValue()) / agent.getAssetValue();

    }

    private HasLeverageConstraint agent;
}
