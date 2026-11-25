package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

/** The stock service doesn't stock the item asked about; the message is the service's own reason. */
public final class NoSuchItemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoSuchItemException(final String reason) {
        super(reason);
    }
}
