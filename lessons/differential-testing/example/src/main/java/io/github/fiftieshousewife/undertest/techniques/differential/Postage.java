package io.github.fiftieshousewife.undertest.techniques.differential;

/** What it costs to post a parcel. */
public interface Postage {

    int MAX_GRAMS = 5_000;

    /** The price in pence for a parcel of this weight, from 1 gram to 5 kilograms. */
    int pence(int grams);
}
