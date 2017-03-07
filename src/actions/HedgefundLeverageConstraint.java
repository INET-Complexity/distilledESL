package actions;

import agents.Hedgefund;

public class HedgefundLeverageConstraint {

     private Hedgefund hedgefund;
     private double leverageTarget;
     private double leverageBuffer;
     private double leverageMin;

     private static final double DEFAULT_LEVERAGE_TARGET = 2.0;
     private static final double DEFAULT_LEVERAGE_BUFFER = 1.0;
     private static final double DEFAULT_LEVERAGE_MIN = 0.0;



     public HedgefundLeverageConstraint(Hedgefund hedgefund, double leverageTarget, double leverageBuffer, double leverageMin) {
         this.hedgefund = hedgefund;
         this.leverageTarget = leverageTarget;
         this.leverageBuffer = leverageBuffer;
         this.leverageMin = leverageMin;

         assert((leverageTarget >= leverageBuffer) && (leverageBuffer >= leverageMin));
     }

     public HedgefundLeverageConstraint(Hedgefund hedgefund) {
         this(hedgefund, DEFAULT_LEVERAGE_TARGET, DEFAULT_LEVERAGE_BUFFER, DEFAULT_LEVERAGE_MIN);
     }

     public boolean isBelowBuffer() {
         return (hedgefund.getLeverage() < hedgefund.getEffectiveMinLeverage() + leverageBuffer);
     }

     public boolean isBelowMin() {
         return (hedgefund.getLeverage() < hedgefund.getEffectiveMinLeverage() + leverageMin);
     }

     public double getAmountToDelever() {
         return (hedgefund.getMainLedger().getAssetValue() * (1 - (hedgefund.getLeverage() / (hedgefund.getEffectiveMinLeverage() + leverageTarget))));
     }

}
