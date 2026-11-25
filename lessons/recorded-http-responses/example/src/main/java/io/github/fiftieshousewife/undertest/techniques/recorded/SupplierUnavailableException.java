package io.github.fiftieshousewife.undertest.techniques.recorded;

/** The supplier's service couldn't answer: try again later. */
public final class SupplierUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SupplierUnavailableException(final String message) {
        super(message);
    }

    public SupplierUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
