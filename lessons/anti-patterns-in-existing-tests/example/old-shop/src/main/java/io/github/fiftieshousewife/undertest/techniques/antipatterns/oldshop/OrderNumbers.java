package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import java.util.concurrent.atomic.AtomicLong;

/** Order numbers, counted from 1 for the whole shop. */
public final class OrderNumbers {

    private static final AtomicLong LAST = new AtomicLong();

    private OrderNumbers() {
    }

    public static long next() {
        return LAST.incrementAndGet();
    }
}
