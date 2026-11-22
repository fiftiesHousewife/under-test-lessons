package io.github.fiftieshousewife.undertest.techniques.openapi;

/** A parcel: its id, where it is now (such as OUT_FOR_DELIVERY), and its weight in grams. */
public record Parcel(String id, String status, int weightGrams) {
}
