package io.github.fiftieshousewife.undertest.techniques.changedcode;

/**
 * OLD CODE, NO TESTS: the VAT in pence on a net amount, by the kind of goods. Untouched by the change.
 */
public class VatRates {

    /** The VAT on this net amount in pence: none on books and children's clothes, 5% on fuel, 20% on the rest. */
    public long vat(final String kind, final long net) {
        return switch (kind) {
            case "book", "childrens-clothes" -> 0;
            case "fuel" -> net * 5 / 100;
            default -> net * 20 / 100;
        };
    }
}
