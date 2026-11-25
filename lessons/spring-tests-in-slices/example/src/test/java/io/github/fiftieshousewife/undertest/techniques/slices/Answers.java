package io.github.fiftieshousewife.undertest.techniques.slices;

import io.github.fiftieshousewife.undertest.techniques.slices.fixed.DeliveryView;
import io.github.fiftieshousewife.undertest.techniques.slices.today.TodaysDeliveryView;

/* The answer the slice tests write: the fixed one, or today's with -Pcode=today. */
final class Answers {

    private Answers() {
    }

    static DeliveryAnswer forDelivery(final Delivery delivery, final String tracking) {
        return "today".equals(System.getProperty("code"))
                ? TodaysDeliveryView.of(delivery, tracking) : DeliveryView.of(delivery, tracking);
    }
}
