package io.github.fiftieshousewife.undertest.techniques.architecture.storage;

import java.util.LinkedHashMap;
import java.util.Map;

/** The storage layer: where orders are kept. Only the service layer should use it. */
public final class OrderStore {

    private final Map<String, Integer> pence = new LinkedHashMap<>();

    public void save(final String id, final int amountPence) {
        pence.put(id, amountPence);
    }

    public int count() {
        return pence.size();
    }
}
