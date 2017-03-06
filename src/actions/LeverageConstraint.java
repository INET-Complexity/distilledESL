package actions;

import agents.Agent;

public class LeverageConstraint {
    private Agent agent;
    private double leverageTarget;
    private double leverageBuffer;
    private double leverageMin;

    private static final double DEFAULT_LEVERAGE_TARGET = 4.0;
    private static final double DEFAULT_LEVERAGE_BUFFER = 3.0;
    private static final double DEFAULT_LEVERAGE_MIN = 1.0;



    public LeverageConstraint(Agent agent, double leverageTarget, double leverageBuffer, double leverageMin) {
        this.agent = agent;
        this.leverageTarget = leverageTarget;
        this.leverageBuffer = leverageBuffer;
        this.leverageMin = leverageMin;

        assert((leverageTarget >= leverageBuffer) && (leverageBuffer >= leverageMin));
    }

    public LeverageConstraint(Agent agent) {
        this(agent, DEFAULT_LEVERAGE_TARGET, DEFAULT_LEVERAGE_BUFFER, DEFAULT_LEVERAGE_MIN);
    }

    public boolean isBelowBuffer() {
        return (agent.getLeverage() < leverageBuffer);
    }

    public boolean isBelowMin() {
        return (agent.getLeverage() < leverageMin);
    }

    public double getAmountToDelever() {
        return (agent.getMainLedger().getAssetValue() * (1 - (agent.getLeverage() / leverageTarget) ));
    }
}
