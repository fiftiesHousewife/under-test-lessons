package io.github.fiftieshousewife.undertest.techniques.antipatterns;

/**
 * One anti-pattern found in one test: its kind (such as "sleeps"), the test (class and method) and where it is (file
 * and line).
 */
public record Finding(String kind, String test, String where) {
}
