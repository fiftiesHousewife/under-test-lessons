package io.github.fiftieshousewife.undertest.techniques.cleaning;

import java.util.function.UnaryOperator;

/** Cleans a captured day of personal data before it is kept with the tests. */
public interface Cleaning {

    /** The fixed text that replaces a value nothing needs. */
    String REMOVED = "REMOVED";

    /** Replaces a value with the fixed text, leaving empty values empty. */
    UnaryOperator<String> REMOVE = value -> value.isEmpty() ? value : REMOVED;

    /** The captured day with its personal data replaced. */
    Capture clean(Capture capture);
}
