package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** The warehouse's system: stock reserved for an order, and orders sent out. */
public interface Warehouse {

    boolean reserve(long orderNumber);

    void send(long orderNumber);
}
