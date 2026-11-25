package io.github.fiftieshousewife.undertest.techniques.masking;

import java.util.function.UnaryOperator;

/**
 * Masks the personal data in a day of real data before it is kept with the tests. A value nothing needs is replaced
 * with the fixed text {@code REMOVED}; an empty value stays empty.
 */
public interface Masking {

    String REMOVED = "REMOVED";

    UnaryOperator<String> REMOVE = value -> value.isEmpty() ? value : REMOVED;

    /** The day's data with its personal data replaced. */
    Capture mask(Capture capture);
}
