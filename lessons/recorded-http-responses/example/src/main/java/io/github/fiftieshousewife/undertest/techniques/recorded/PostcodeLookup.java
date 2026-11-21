package io.github.fiftieshousewife.undertest.techniques.recorded;

import java.util.Optional;

/** Finds the town for a postcode, using a supplier's web service. */
public interface PostcodeLookup {

    /** The town, or nothing if the supplier doesn't know the postcode. */
    Optional<String> town(String postcode);
}
