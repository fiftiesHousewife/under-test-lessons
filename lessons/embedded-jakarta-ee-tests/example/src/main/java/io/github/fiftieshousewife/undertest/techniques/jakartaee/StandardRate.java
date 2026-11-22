package io.github.fiftieshousewife.undertest.techniques.jakartaee;

import jakarta.enterprise.context.ApplicationScoped;

/** The standard rate: 20%. */
@ApplicationScoped
@Standard
public class StandardRate implements TaxRate {

    @Override
    public int percent() {
        return 20;
    }
}
