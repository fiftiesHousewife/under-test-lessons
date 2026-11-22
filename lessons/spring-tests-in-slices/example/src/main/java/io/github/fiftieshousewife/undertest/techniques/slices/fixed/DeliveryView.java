package io.github.fiftieshousewife.undertest.techniques.slices.fixed;

import io.github.fiftieshousewife.undertest.techniques.slices.Delivery;
import io.github.fiftieshousewife.undertest.techniques.slices.DeliveryAnswer;
import java.time.LocalDate;

/** FIXED: the due date is written as the description promises, an ISO date such as 2026-11-23. */
public record DeliveryView(String reference, LocalDate dueOn, String tracking) implements DeliveryAnswer {

    public static DeliveryView of(final Delivery delivery, final String tracking) {
        return new DeliveryView(delivery.getReference(), delivery.getDueOn(), tracking);
    }
}
