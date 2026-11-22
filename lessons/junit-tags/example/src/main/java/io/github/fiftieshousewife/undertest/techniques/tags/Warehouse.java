package io.github.fiftieshousewife.undertest.techniques.tags;

/** The warehouse's stock system, asked how many of an item are on the shelves. */
public interface Warehouse {

    int inStock(String item);
}
