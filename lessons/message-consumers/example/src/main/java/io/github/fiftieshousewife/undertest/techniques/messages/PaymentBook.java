package io.github.fiftieshousewife.undertest.techniques.messages;

/** Where the shop records payments: its database, in production. */
public interface PaymentBook {

    /** Records a payment, or throws {@link PaymentNotRecorded} if the book is busy and can't record it now. */
    void record(Payment payment);

    /** Whether a payment for this order is already recorded. */
    boolean has(String order);
}
