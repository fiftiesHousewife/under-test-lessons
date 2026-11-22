package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

/** What the order screen shows for an item: its stock-keeping code and how many are available to order. */
public record StockLevel(String sku, int available) {
}
