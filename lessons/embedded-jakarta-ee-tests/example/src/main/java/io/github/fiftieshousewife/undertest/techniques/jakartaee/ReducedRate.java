package io.github.fiftieshousewife.undertest.techniques.jakartaee;

import jakarta.enterprise.context.ApplicationScoped;

/** The reduced rate, 5%, for things like children's car seats. Added later; it has no qualifier. */
@ApplicationScoped
public class ReducedRate implements TaxRate {

    @Override
    public int percent() {
        return 5;
    }
}
