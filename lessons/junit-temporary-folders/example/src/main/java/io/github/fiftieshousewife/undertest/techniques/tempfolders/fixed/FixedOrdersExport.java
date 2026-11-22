package io.github.fiftieshousewife.undertest.techniques.tempfolders.fixed;

import io.github.fiftieshousewife.undertest.techniques.tempfolders.ShopOrder;
import io.github.fiftieshousewife.undertest.techniques.tempfolders.OrdersExport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/** FIXED: leaves cancelled orders out, and always writes the file. */
public final class FixedOrdersExport implements OrdersExport {

    @Override
    public void write(final List<ShopOrder> orders, final Path folder) throws IOException {
        final Stream<String> placed = orders.stream().filter(order -> !order.cancelled()).map(ShopOrder::csvLine);
        Files.write(folder.resolve(FILE_NAME), Stream.concat(Stream.of(HEADER), placed).toList());
    }
}
