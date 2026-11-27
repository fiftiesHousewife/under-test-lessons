package io.github.fiftieshousewife.undertest.techniques.properties;

import java.util.ArrayList;
import java.util.List;

/** Everyone pays the same, and the cents left over go one each to the first people, so no share differs by more. */
public class FairSplitter implements BillSplitter {

    @Override
    public List<Integer> split(final int totalCents, final int people) {
        final List<Integer> shares = new ArrayList<>();
        for (int i = 0; i < people; i++) {
            shares.add(totalCents / people + (i < totalCents % people ? 1 : 0));
        }
        return shares;
    }
}
