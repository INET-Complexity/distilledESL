package behaviours;

import agents.Investor;

public class InvestorBehaviour extends Behaviour {
    private Investor me;

    public InvestorBehaviour(Investor me) {
        super(me);
        this.me = me;
    }

    @Override
    protected void chooseActions() {

        // We must compute NAV (Net Asset Value).
    }
}
