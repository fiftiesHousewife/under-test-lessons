package io.github.fiftieshousewife.undertest.techniques.metamorphic;

/** One line of a basket: a product, its price in whole yen, and how many of it. */
public record Line(String product, int priceYen, int quantity) {

    /** What the line costs before any discount: its price times how many. */
    public int costYen() {
        return priceYen * quantity;
    }
}
