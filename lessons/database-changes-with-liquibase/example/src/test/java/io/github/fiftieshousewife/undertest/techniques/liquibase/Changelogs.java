package io.github.fiftieshousewife.undertest.techniques.liquibase;

/** The changelog the tests apply: the fixed one, or today's with -Pcode=today. */
final class Changelogs {

    private Changelogs() {
    }

    static String current() {
        return "today".equals(System.getProperty("code")) ? "db/changelog-today.yaml" : "db/changelog.yaml";
    }
}
