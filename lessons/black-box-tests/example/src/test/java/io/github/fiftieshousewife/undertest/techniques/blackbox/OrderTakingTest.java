package io.github.fiftieshousewife.undertest.techniques.blackbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * BEFORE: unit tests of the order rules, with stand-ins for the warehouse and the database. They pass, and never run
 * the web server, the warehouse call or the SQL the application really uses.
 */
class OrderTakingTest {

    private final List<PlacedOrder> kept = new ArrayList<>();

    @Test
    void anOrderWithStockIsPlacedAndKept() {
        final OrderTaking orders = new OrderTaking((sku, quantity) -> Optional.of("W-881"), kept::add);
        final Optional<PlacedOrder> placed = orders.place(new OrderRequest("C-42", "PEN-01", 3));
        assertAll(
                () -> assertThat(placed).map(PlacedOrder::orderId).contains("O-1001"),
                () -> assertThat(kept).extracting(PlacedOrder::reservationId).containsExactly("W-881"));
    }

    @Test
    void anOrderWithoutStockIsRefusedAndNotKept() {
        final OrderTaking orders = new OrderTaking((sku, quantity) -> Optional.empty(), kept::add);
        assertAll(
                () -> assertThat(orders.place(new OrderRequest("C-42", "PEN-01", 300))).isEmpty(),
                () -> assertThat(kept).isEmpty());
    }
}
