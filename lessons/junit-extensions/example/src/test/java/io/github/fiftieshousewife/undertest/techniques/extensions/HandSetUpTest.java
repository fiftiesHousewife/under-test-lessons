package io.github.fiftieshousewife.undertest.techniques.extensions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/* BEFORE: sets the shop's settings by hand, once, and never puts them back. A change in one test breaks the next. */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HandSetUpTest {

    @BeforeAll
    static void setUpShopSettings() {
        ShopSettings.set(ShopSettings.PRICES_INCLUDE_VAT, "true");
        ShopSettings.set(ShopSettings.VAT_PERCENT, "20");
    }

    @Test
    @Order(1)
    void tradeInvoiceShowsPriceWithoutVat() {
        assertThat(Invoices.current().line("Car seat", 10_000)).isEqualTo("Car seat  £100.00 + VAT");
    }

    @Test
    @Order(2)
    void shopReceiptShowsPriceWithVat() {
        assertThat(new ShopReceipt().line("Car seat", 10_000)).isEqualTo("Car seat  £120.00");
    }
}
