package actions;

import agents.Hedgefund;
import demos.Parameters;

public class HedgefundLeverageConstraint {

     private Hedgefund hf;

     public HedgefundLeverageConstraint(Hedgefund hf) {
         this.hf = hf;
     }

     public boolean isBelowBuffer() {
         return (hf.getLeverage() < hf.getEffectiveMinLeverage() + Parameters.HF_DEFAULT_LEVERAGE_BUFFER);
     }

     public boolean isBelowMin() {
         return (hf.getLeverage() < hf.getEffectiveMinLeverage());
     }

     private double getLeverageTarget() {
         return hf.getEffectiveMinLeverage() + Parameters.HF_DEFAULT_LEVERAGE_TARGET;
     }


     public double getAmountToDelever() {
         return hf.getEquityValue() * (1.0 / hf.getLeverage() - 1.0 / getLeverageTarget());
     }

}
