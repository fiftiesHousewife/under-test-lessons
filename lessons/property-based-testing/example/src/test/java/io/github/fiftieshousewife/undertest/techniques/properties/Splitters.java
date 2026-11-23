package io.github.fiftieshousewife.undertest.techniques.properties;

import java.util.Set;

/**
 * The splitter the tests run against: the fair one, or the one in use today with -Pcode=today, and with
 * -Pcode=answer, which runs the exercise's answer beside today's tests.
 */
public final class Splitters {

    private Splitters() {
    }

    public static BillSplitter current() {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        return today ? new FirstPayerSplitter() : new FairSplitter();
    }
}
