package behaviours;

import agents.Agent;
import demos.BoEDemo;

public class DefaultException extends Exception {
    private Agent me;
    private TypeOfDefault typeOfDefault;
    private int timestep;
    private double equityAtDefault;
    private double lcrAtDefault;

    public DefaultException(Agent me, TypeOfDefault typeOfDefault) {
        this.me = me;
        this.typeOfDefault = typeOfDefault;
        this.timestep = BoEDemo.getTime();
        this.equityAtDefault = me.getEquityValue();
        this.lcrAtDefault = me.getLCR();

    }

    public enum TypeOfDefault {
        LIQUIDITY,
        SOLVENCY,
        FAILED_MARGIN_CALL
    }

    public Agent getAgent() {
        return me;
    }

    public TypeOfDefault getTypeOfDefault() {
        return typeOfDefault;
    }

    public int getTimestep() {
        return timestep;
    }
}
