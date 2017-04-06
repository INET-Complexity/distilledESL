package org.economicsl;

/**
 * Created by taghawi on 30/03/17.
 */
public class GoodMessage {
    public final String good_name;
    public final double amount;
    public final double value;

    public GoodMessage(String good_name, double amount, double value) {
        this.good_name = good_name;
        this.amount = amount;
        this.value = value;
    }
}
