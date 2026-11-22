package io.github.fiftieshousewife.undertest.techniques.examples;

/** What the customer gets back, in the words the returns team uses. */
public enum Refund {
    TO_CARD("refund to card"),
    STORE_CREDIT("store credit"),
    NOTHING("nothing");

    private final String teamWords;

    Refund(final String words) {
        this.teamWords = words;
    }

    /** The returns team's words for this refund, as their examples write it. */
    public String words() {
        return teamWords;
    }
}
