package io.github.fiftieshousewife.undertest.techniques.blackbox;

/** An order the shop has accepted, with the warehouse's id for the stock it put aside. */
public record PlacedOrder(String orderId, OrderRequest request, String reservationId) {
}
