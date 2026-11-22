package io.github.fiftieshousewife.undertest.techniques.blackbox;

/** What a customer asks for: a quantity of one product, by its stock code. */
public record OrderRequest(String customer, String sku, int quantity) {
}
