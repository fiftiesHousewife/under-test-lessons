package io.github.fiftieshousewife.undertest.techniques.faults;

/** Asks the supplier how many of an item it has in stock, before the shop promises a delivery date. */
public interface StockCheck {

    /** The supplier's stock of the item, or why the shop couldn't find out. */
    StockAnswer check(String sku);
}
