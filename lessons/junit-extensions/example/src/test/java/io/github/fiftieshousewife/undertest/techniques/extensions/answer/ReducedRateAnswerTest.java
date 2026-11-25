package io.github.fiftieshousewife.undertest.techniques.extensions.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.extensions.Invoices;
import io.github.fiftieshousewife.undertest.techniques.extensions.ShopReceipt;
import io.github.fiftieshousewife.undertest.techniques.extensions.ShopSettings;
import io.github.fiftieshousewife.undertest.techniques.extensions.StandardShopSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/*
 * THE EXERCISE'S ANSWER: a car seat sells at the reduced VAT rate of 5%. After a delivery note, a receipt must still
 * charge 5%. The extension checks the settings against its standard at the end of each test, and the standard rate is
 * 20, so only a test that looks inside finds a note that puts the rate back to 20. Run it against today's delivery
 * note with {@code -Pcode=answer}.
 */
@ExtendWith(StandardShopSettings.class)
class ReducedRateAnswerTest {

    @Test
    void aReceiptAfterADeliveryNoteKeepsTheReducedRate() {
        ShopSettings.set(ShopSettings.VAT_PERCENT, "5");
        Invoices.deliveryNote().line("Car seat", 10_000);
        final String receipt = new ShopReceipt().line("Car seat", 10_000);
        ShopSettings.set(ShopSettings.VAT_PERCENT, "20");

        assertThat(receipt).as("a car seat's receipt at the reduced rate").isEqualTo("Car seat  £105.00");
    }
}
