package io.github.fiftieshousewife.undertest.techniques.masking;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** The reports the shop runs on a day's orders and payments, and the tests run on a day of real data. */
public final class DayReport {

    private DayReport() {
    }

    /** How many orders each customer placed, customers told apart by email address, largest first. */
    public static List<Integer> ordersPerCustomer(final Capture day) {
        final Map<String, Integer> orders = day.orders().rows().stream()
                .collect(Collectors.groupingBy(order -> order.get("email"), Collectors.summingInt(order -> 1)));
        return orders.values().stream().sorted(Comparator.reverseOrder()).toList();
    }

    /** Each account whose orders and payments differ: pence owed (below zero, paid too much), smallest first. */
    public static List<Integer> unsettledAccounts(final Capture day) {
        final Map<String, Integer> ordered = totals(day.orders());
        final Map<String, Integer> paid = totals(day.payments());
        return ordered.entrySet().stream().map(account -> account.getValue() - paid.getOrDefault(account.getKey(), 0))
                .filter(balance -> balance != 0).sorted().toList();
    }

    private static Map<String, Integer> totals(final CapturedFile file) {
        return file.rows().stream().collect(Collectors.groupingBy(row -> row.get("account"),
                Collectors.summingInt(row -> Integer.parseInt(row.get("pence")))));
    }
}
