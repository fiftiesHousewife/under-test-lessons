package io.github.fiftieshousewife.undertest.techniques.database;

/** Which query the tests use: the fixed one, or today's with -Pcode=today. */
final class Queries {

    private Queries() {
    }

    /** True for the fixed query; false for today's, with -Pcode=today. */
    static boolean fixed() {
        return !"today".equals(System.getProperty("code"));
    }
}
