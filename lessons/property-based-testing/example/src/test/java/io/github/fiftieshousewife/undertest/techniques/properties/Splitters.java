package io.github.fiftieshousewife.undertest.techniques.properties;

/** The splitter the tests run against: the fair one, or the one in use today with -Pcode=today. */
final class Splitters {

    private Splitters() {
    }

    static BillSplitter current() {
        return "today".equals(System.getProperty("code")) ? new FirstPayerSplitter() : new FairSplitter();
    }
}
