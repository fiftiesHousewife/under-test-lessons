package io.github.fiftieshousewife.undertest.techniques.flags;

/** What delivery costs for a basket: €3.99, or free from €50. */
public interface ShippingRules {

    int cents(int basketCents);
}
