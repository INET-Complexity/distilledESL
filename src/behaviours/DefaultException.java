package behaviours;

import agents.StressAgent;
import demos.Model;

public class DefaultException extends Exception {
    private StressAgent me;
    private TypeOfDefault typeOfDefault;
    private int timestep;
    private double equityAtDefault;
    private double lcrAtDefault;

    public DefaultException(StressAgent me, TypeOfDefault typeOfDefault) {
        this.me = me;
        this.typeOfDefault = typeOfDefault;
        this.timestep = Model.getTime();
        this.equityAtDefault = me.getEquityValue();
        this.lcrAtDefault = me.getLCR();

    }

    public enum TypeOfDefault {
        LIQUIDITY,
        SOLVENCY,
        FAILED_MARGIN_CALL
    }

    public StressAgent getAgent() {
        return me;
    }

    public TypeOfDefault getTypeOfDefault() {
        return typeOfDefault;
    }

    public int getTimestep() {
        return timestep;
    }
}
