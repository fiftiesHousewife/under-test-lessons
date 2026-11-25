package io.github.fiftieshousewife.undertest.techniques.masking;

import java.util.function.UnaryOperator;

/** Masks a day of real data of personal data before it is kept with the tests. */
public interface Masking {

    /** The fixed text that replaces a value nothing needs. */
    String REMOVED = "REMOVED";

    /** Replaces a value with the fixed text, leaving empty values empty. */
    UnaryOperator<String> REMOVE = value -> value.isEmpty() ? value : REMOVED;

    /** The day's data with its personal data replaced. */
    Capture mask(Capture capture);
}
