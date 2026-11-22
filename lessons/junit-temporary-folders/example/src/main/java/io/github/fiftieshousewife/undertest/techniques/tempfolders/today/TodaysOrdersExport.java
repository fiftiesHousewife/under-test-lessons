package io.github.fiftieshousewife.undertest.techniques.tempfolders.today;

import io.github.fiftieshousewife.undertest.techniques.tempfolders.ShopOrder;
import io.github.fiftieshousewife.undertest.techniques.tempfolders.OrdersExport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** TODAY: meant to skip a cancelled order, but returns instead, so on a day with one it writes no file at all. */
public final class TodaysOrdersExport implements OrdersExport {

    @Override
    public void write(final List<ShopOrder> orders, final Path folder) throws IOException {
        final List<String> lines = new ArrayList<>(List.of(HEADER));
        for (final ShopOrder order : orders) {
            if (order.cancelled()) {
                return;
            }
            lines.add(order.csvLine());
        }
        Files.write(folder.resolve(FILE_NAME), lines);
    }
}
