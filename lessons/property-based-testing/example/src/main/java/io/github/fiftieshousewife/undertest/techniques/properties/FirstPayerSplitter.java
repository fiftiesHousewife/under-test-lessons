package io.github.fiftieshousewife.undertest.techniques.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The splitter in use today: everyone pays the same, and the first person also pays whatever is left over. With a
 * remainder of one cent that looks fair, so example tests pass; with more left over, the first person pays several
 * cents more than everyone else.
 */
public class FirstPayerSplitter implements BillSplitter {

    @Override
    public List<Integer> split(final int totalCents, final int people) {
        final List<Integer> shares = new ArrayList<>(Collections.nCopies(people, totalCents / people));
        shares.set(0, shares.getFirst() + totalCents % people);
        return shares;
    }
}
