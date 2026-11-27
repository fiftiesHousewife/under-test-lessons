package io.github.fiftieshousewife.undertest.techniques.jakartaee.today;

import io.github.fiftieshousewife.undertest.techniques.jakartaee.Checkout;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.TaxRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** TODAY: asks the container for "a tax rate". Since the reduced rate arrived, the container picks that one. */
@ApplicationScoped
public class TodaysCheckout implements Checkout {

    private final TaxRate rate;

    @Inject
    public TodaysCheckout(final TaxRate rate) {
        this.rate = rate;
    }

    @Override
    public int totalCents(final int netCents) {
        return Checkout.withVat(netCents, rate);
    }
}
