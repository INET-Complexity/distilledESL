package economicsl;

/**
 * Exception thrown to indicate that there is insufficient
 * balance inventory.
 * @author Davoud adapted from https://newcircle.com/bookshelf/java_fundamentals_tutorial/exceptions
 * @version 1.0
 */
public class NotEnoughGoods extends Exception {
    private final double available;
    private final double required;

    /**
     * Constructor.
     * @param available available good in inventory
     * @param required required good
     */
    public NotEnoughGoods(String name, double available, double required) {
        super(name + " available " + available + " but required " + required);
        this.available = available;
        this.required = required;
    }

    /**
     * Get available inventory
     * @return available inventory
     */
    public double getAvailable() {
        return available;
    }

    /**
     * Get required amount of good
     * @return required amount of good
     */
    public double getRequired() {
        return required;
    }

    /**
     * Get the difference between required and available inventory
     * @return required - available
     */
    public double getDifference() {
        return required - available;
    }
}
