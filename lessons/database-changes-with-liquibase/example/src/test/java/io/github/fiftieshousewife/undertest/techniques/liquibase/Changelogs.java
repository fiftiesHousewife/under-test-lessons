package io.github.fiftieshousewife.undertest.techniques.liquibase;

import java.util.Set;

/** The changelog the tests apply: the fixed one, or today's with -Pcode=today and -Pcode=answer. */
public final class Changelogs {

    private Changelogs() {
    }

    public static String current() {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        return today ? "db/changelog-today.yaml" : "db/changelog.yaml";
    }
}
