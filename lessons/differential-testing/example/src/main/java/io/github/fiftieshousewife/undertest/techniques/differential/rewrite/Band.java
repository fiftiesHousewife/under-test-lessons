package io.github.fiftieshousewife.undertest.techniques.differential.rewrite;

/** One row of the price list: parcels up to this weight cost this much. */
public record Band(int upToGrams, int pence) {
}
