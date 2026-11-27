package io.github.fiftieshousewife.undertest.techniques.commandline.fixed;

import io.github.fiftieshousewife.undertest.techniques.commandline.Payment;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * FIXED: the same program, given how to print and how to end. It still reads its environment variable and standard
 * input. Whatever runs it chooses: Java's own printing and exit, or, in a test, its own that only record.
 */
public final class PaymentImport {

    private final Consumer<String> print;
    private final IntConsumer exit;

    public PaymentImport(final Consumer<String> print, final IntConsumer exit) {
        this.print = print;
        this.exit = exit;
    }

    /** Imports the payments, then ends once: with 0 if all were accepted, or 1 at the first one over the limit. */
    public void run() {
        final long limit = Long.parseLong(System.getenv("PAYMENT_LIMIT_PAISE"));
        final List<Payment> payments = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
            .lines().map(Payment::parse).toList();
        exit.accept(importPayments(payments, limit));
    }

    private int importPayments(final List<Payment> payments, final long limit) {
        long total = 0;
        for (final Payment payment : payments) {
            if (payment.isOver(limit)) {
                print.accept("over the limit: " + payment.account());
                return 1;
            }
            print.accept("accepted " + payment.account() + " " + payment.paise());
            total += payment.paise();
        }
        print.accept(payments.size() + " payments, " + total + " paise");
        return 0;
    }
}
