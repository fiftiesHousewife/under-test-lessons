package io.github.fiftieshousewife.undertest.techniques.properties;

import java.util.ArrayList;
import java.util.List;

/** Everyone pays the same, and the pence left over go one each to the first people, so no share differs by more. */
public class FairSplitter implements BillSplitter {

    @Override
    public List<Integer> split(final int totalPence, final int people) {
        final List<Integer> shares = new ArrayList<>();
        for (int i = 0; i < people; i++) {
            shares.add(totalPence / people + (i < totalPence % people ? 1 : 0));
        }
        return shares;
    }
}
