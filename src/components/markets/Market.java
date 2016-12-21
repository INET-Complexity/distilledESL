package components.markets;

import ESL.inventory.Good;
import ESL.inventory.Item;

public interface Market {
    void putForSale(double amount);
    boolean canBeSoldHere(Item item);
}
