package io.github.fiftieshousewife.undertest.techniques.metamorphic;

/**
 * Prices a shop's basket in whole yen, with the shop's spend discount: 20% off a basket of 10,000 yen or more. There's
 * no record of exactly how the discount should work, so no test can state the right total for every basket.
 */
public interface BasketPricing {

    /** What the customer pays for the basket, in whole yen. */
    int totalYen(Basket basket);
}
