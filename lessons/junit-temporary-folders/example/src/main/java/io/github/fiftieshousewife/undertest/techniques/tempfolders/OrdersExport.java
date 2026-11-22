package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** The end-of-day export: writes orders.csv, a header and one line per order, for the accounts system to read. */
public interface OrdersExport {

    String FILE_NAME = "orders.csv";
    String HEADER = "order,total";

    /** Writes the day's orders, leaving out cancelled ones, to orders.csv in the given folder. */
    void write(List<ShopOrder> orders, Path folder) throws IOException;
}
