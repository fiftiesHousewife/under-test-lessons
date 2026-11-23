package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import java.util.ArrayList;
import java.util.List;

/** A shop's basket: the prices of its items, with 5% off a basket of £50 or more. */
public class Basket {

    private final List<Long> prices = new ArrayList<>();

    public Basket add(final long pricePence) {
        prices.add(pricePence);
        return this;
    }

    public long totalPence() {
        final long total = prices.stream().mapToLong(Long::longValue).sum();
        return total >= 5000 ? total * 95 / 100 : total;
    }
}
