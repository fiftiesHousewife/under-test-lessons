package io.github.fiftieshousewife.undertest.techniques.properties;

import java.util.List;

/** Shares a bill, in cents, between a number of people. */
public interface BillSplitter {

    List<Integer> split(int totalCents, int people);
}
