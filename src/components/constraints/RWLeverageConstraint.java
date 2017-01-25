package components.constraints;

public class RWLeverageConstraint {

    public RWLeverageConstraint(HasRWLeverageConstraint agent) {
        this.agent = agent;
    }

    public double getRWLeverage() {
        return (agent.getRWAssetValue() - agent.getRWLiabilityValue()) / agent.getRWAssetValue();
    }


    private HasRWLeverageConstraint agent;

}
