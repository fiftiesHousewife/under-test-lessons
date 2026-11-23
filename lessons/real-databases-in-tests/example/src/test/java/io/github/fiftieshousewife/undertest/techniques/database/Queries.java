package io.github.fiftieshousewife.undertest.techniques.database;

import java.util.Set;

/** Which query the tests use: the fixed one, or today's with -Pcode=today and -Pcode=answer. */
public final class Queries {

    private Queries() {
    }

    /** True for the fixed query; false for today's, with -Pcode=today, or -Pcode=answer for the exercise's answer. */
    public static boolean fixed() {
        return !Set.of("today", "answer").contains(System.getProperty("code"));
    }
}
