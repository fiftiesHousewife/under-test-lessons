package io.github.fiftieshousewife.undertest.techniques.jakartaee.fixed;

import io.github.fiftieshousewife.undertest.techniques.jakartaee.Checkout;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.Standard;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.TaxRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** FIXED: asks the container for the standard rate, by name. */
@ApplicationScoped
public class StandardCheckout implements Checkout {

    private final TaxRate rate;

    @Inject
    public StandardCheckout(@Standard final TaxRate rate) {
        this.rate = rate;
    }

    @Override
    public int totalCents(final int netCents) {
        return Checkout.withVat(netCents, rate);
    }
}
