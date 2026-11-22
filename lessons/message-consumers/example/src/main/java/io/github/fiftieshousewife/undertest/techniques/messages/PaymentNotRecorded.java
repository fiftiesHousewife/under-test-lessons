package io.github.fiftieshousewife.undertest.techniques.messages;

import java.io.Serial;

/** A payment message that could not be recorded: it can't be read, or the payment book is busy. */
public final class PaymentNotRecorded extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentNotRecorded(final String message) {
        super(message);
    }
}
