package io.github.fiftieshousewife.undertest.techniques.messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A payment book in memory that is busy the first time it sees each of the given orders, as a database can be. */
final class BusyOnceBook implements PaymentBook {

    private final Set<String> busyFor;
    private final List<Payment> payments = new ArrayList<>();

    BusyOnceBook(final String... busyFor) {
        this.busyFor = new HashSet<>(List.of(busyFor));
    }

    @Override
    public void record(final Payment payment) {
        if (busyFor.remove(payment.order())) {
            throw new PaymentNotRecorded("the payment book is busy");
        }
        payments.add(payment);
    }

    @Override
    public boolean has(final String order) {
        return payments.stream().anyMatch(payment -> payment.order().equals(order));
    }

    List<Payment> recorded() {
        return List.copyOf(payments);
    }
}
