package end_to_end_test;

import agents.Agent;


/**
 * Created by taghawi on 28/03/17.
 */
public class End2EndTestGive {
    public static final int NUM_AGENTS = 15;
    public static final int ROUNDS = 16;
    private MessageAgent[] children;
    private GiveAgent[] giveandreceives;

    public void init() {
        giveandreceives = new GiveAgent[NUM_AGENTS];
        for(int i = 0; i < NUM_AGENTS; i++) {
            giveandreceives[i] = new GiveAgent(Integer.toString(i), 1, 0);
        }
        giveandreceives[0].getMainLedger().addGoods("ball", 2, 5.50);
        System.out.print(giveandreceives[0]);

    }

    private void run() {
        for (int time = 0; time < ROUNDS; time++) {
            System.out.println("\nTime step: " + time+ "\n^^^^^^^^^^^^^");
            for (int i = 0; i < giveandreceives.length; i++) {
                if (giveandreceives[i].getMainLedger().getGood("ball") > 0.9) {
                    System.out.print(i);
                    System.out.print(" has ball ");
                    System.out.print(giveandreceives[i].getMainLedger().getGood("ball"));
                }
                if (i + 1 < giveandreceives.length) {
                    giveandreceives[i].give(giveandreceives[i + 1]);
                }
            }
            for (Agent undergrad: giveandreceives) {
                undergrad.step();
            }

        }
    }


    public static void main(String[] args) {
        End2EndTestGive simulation = new End2EndTestGive();
        simulation.init();
        simulation.run();
    }
}
