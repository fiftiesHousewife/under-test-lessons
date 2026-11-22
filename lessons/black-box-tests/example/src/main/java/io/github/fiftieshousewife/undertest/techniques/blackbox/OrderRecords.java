package io.github.fiftieshousewife.undertest.techniques.blackbox;

/** Where accepted orders are kept. */
public interface OrderRecords {

    void save(PlacedOrder order);
}
