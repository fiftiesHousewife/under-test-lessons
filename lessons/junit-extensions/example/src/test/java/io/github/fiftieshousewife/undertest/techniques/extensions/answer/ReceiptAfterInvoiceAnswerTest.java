package io.github.fiftieshousewife.undertest.techniques.extensions.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.extensions.Invoices;
import io.github.fiftieshousewife.undertest.techniques.extensions.ShopReceipt;
import io.github.fiftieshousewife.undertest.techniques.extensions.StandardShopSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/*
 * The exercise's answer: a trade invoice, then a shop receipt, in one test. The extension only runs between tests, so
 * this asks what a customer at the till would see, whatever order the tests run in.
 */
@ExtendWith(StandardShopSettings.class)
class ReceiptAfterInvoiceAnswerTest {

    @Test
    void aReceiptAfterATradeInvoiceStillIncludesVat() {
        Invoices.current().line("Pushchair", 25_000);

        assertThat(new ShopReceipt().line("Car seat", 10_000)).isEqualTo("Car seat  £120.00");
    }
}
