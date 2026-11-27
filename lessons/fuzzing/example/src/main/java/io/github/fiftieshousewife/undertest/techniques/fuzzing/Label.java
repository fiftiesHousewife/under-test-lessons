package io.github.fiftieshousewife.undertest.techniques.fuzzing;

/** One parcel label: who it goes to, the postcode's area the courier sorts by (SW1A), and the weight in grams. */
public record Label(String name, String area, int grams) {
}
