package io.github.fiftieshousewife.undertest.techniques.extensions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/** AFTER: an extension sets the shop's settings before each test, puts them back after, and fails a test that leaks. */
@ExtendWith(StandardShopSettings.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GuardedSettingsTest {

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
