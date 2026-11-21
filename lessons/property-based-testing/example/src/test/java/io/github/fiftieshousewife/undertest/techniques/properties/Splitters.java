package io.github.fiftieshousewife.undertest.techniques.properties;

/** The splitter the tests run against: the fair one, or the one in use today with -Psplitter=current. */
final class Splitters {

    private Splitters() {
    }

    static BillSplitter current() {
        return "current".equals(System.getProperty("splitter")) ? new FirstPayerSplitter() : new FairSplitter();
    }
}
