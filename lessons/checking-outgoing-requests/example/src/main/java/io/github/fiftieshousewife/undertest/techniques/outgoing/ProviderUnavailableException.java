package io.github.fiftieshousewife.undertest.techniques.outgoing;

/** The payment provider didn't accept the refund: try again later. */
public final class ProviderUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProviderUnavailableException(final String message) {
        super(message);
    }
}
