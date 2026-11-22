package io.github.fiftieshousewife.undertest.techniques.slices;

import java.time.LocalDate;
import java.util.List;

/** Ann's deliveries, one of them already delivered, and one of Bob's. */
final class ExampleDeliveries {

    static final String ANN = "ann";

    private ExampleDeliveries() {
    }

    static Delivery ovenGloves() {
        return due("D-1001", ANN, LocalDate.of(2026, 11, 23));
    }

    static List<Delivery> all() {
        return List.of(
                ovenGloves(),
                due("D-1002", ANN, LocalDate.of(2026, 11, 20)),
                Delivery.builder().reference("D-1000").customer(ANN).dueOn(LocalDate.of(2026, 11, 2))
                        .delivered(true).build(),
                due("D-2001", "bob", LocalDate.of(2026, 11, 21)));
    }

    private static Delivery due(final String reference, final String customer, final LocalDate dueOn) {
        return Delivery.builder().reference(reference).customer(customer).dueOn(dueOn).build();
    }
}
