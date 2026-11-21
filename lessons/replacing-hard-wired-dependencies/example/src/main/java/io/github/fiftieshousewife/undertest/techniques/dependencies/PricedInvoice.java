package io.github.fiftieshousewife.undertest.techniques.dependencies;

import java.util.List;

/** An invoice priced in pounds, charged in euros. */
public interface PricedInvoice {

    /** The total of the lines, in euro cents, rounded to the nearest cent. */
    int totalInEuroCents(List<Integer> linesInPence);
}
