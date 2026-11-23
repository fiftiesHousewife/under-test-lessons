package io.github.fiftieshousewife.undertest.techniques.unplugged;

/** What a product costs, and its delivery, in pence. */
public record Quote(String product, long pricePence, long deliveryPence) {

    public long totalPence() {
        return pricePence + deliveryPence;
    }
}
