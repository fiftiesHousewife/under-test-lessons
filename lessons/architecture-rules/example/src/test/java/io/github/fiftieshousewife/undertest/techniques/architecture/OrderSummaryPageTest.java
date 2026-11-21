package io.github.fiftieshousewife.undertest.techniques.architecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.architecture.service.OrderService;
import io.github.fiftieshousewife.undertest.techniques.architecture.storage.OrderStore;
import io.github.fiftieshousewife.undertest.techniques.architecture.web.fixed.SummaryPage;
import io.github.fiftieshousewife.undertest.techniques.architecture.web.today.TodaysSummaryPage;
import org.junit.jupiter.api.Test;

/**
 * BEFORE: ordinary unit tests. Both versions of the admin page give the right answer, so these pass for both: they
 * check what the code does, and can't see that today's page skips the service layer.
 */
class OrderSummaryPageTest {

    @Test
    void bothPagesCountTheOrders() {
        final OrderStore store = new OrderStore();
        final OrderService service = new OrderService(store);
        service.placeOrder("A-1", 1250);
        service.placeOrder("A-2", 899);

        assertAll(
                () -> assertThat(new TodaysSummaryPage(store).summary()).isEqualTo("Orders: 2"),
                () -> assertThat(new SummaryPage(service).summary()).isEqualTo("Orders: 2"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> service.placeOrder("A-3", 0)));
    }
}
