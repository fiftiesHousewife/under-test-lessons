package io.github.fiftieshousewife.undertest.techniques.messages;

/** Where the shop records payments: its database, in production. */
public interface PaymentBook {

    /** Records a payment, or throws {@link PaymentNotRecorded} if the book is busy and can't record it now. */
    void record(Payment payment);

    /** Whether this payment, by its id, is already recorded: an order can have more than one payment. */
    boolean has(String payment);
}
