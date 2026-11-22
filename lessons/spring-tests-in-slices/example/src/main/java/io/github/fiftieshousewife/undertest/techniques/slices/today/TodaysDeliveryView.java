package io.github.fiftieshousewife.undertest.techniques.slices.today;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.fiftieshousewife.undertest.techniques.slices.Delivery;
import io.github.fiftieshousewife.undertest.techniques.slices.DeliveryAnswer;
import java.time.LocalDate;

/**
 * TODAY: a change for the printed delivery notes gave the due date a day-first pattern, so the service now writes
 * 23/11/2026 where its description promises 2026-11-23. Every caller reading the date as ISO breaks.
 */
public record TodaysDeliveryView(String reference, @JsonFormat(pattern = "dd/MM/yyyy") LocalDate dueOn,
        String tracking) implements DeliveryAnswer {

    public static TodaysDeliveryView of(final Delivery delivery, final String tracking) {
        return new TodaysDeliveryView(delivery.getReference(), delivery.getDueOn(), tracking);
    }
}
