package economicsl.obligations;

/**
 * Created by taghawi on 06/04/17.
 */
public class Simulation {
    public Simulation() {
        this.time = 0;
    }

    public void advance_time() {
        this.time += 1;
    }

    private int time;

    public int getTime() {
        return time;
    }
}
