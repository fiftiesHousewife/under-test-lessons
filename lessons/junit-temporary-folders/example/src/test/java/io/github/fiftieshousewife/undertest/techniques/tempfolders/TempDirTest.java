package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

/** AFTER: JUnit gives each test its own empty folder, and removes it afterwards. */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TempDirTest {

    @Test
    @Order(1)
    void writesOneLinePerOrder(@TempDir final Path folder) throws IOException {
        Exports.current().write(List.of(ShopOrder.placed("1001", 2_500), ShopOrder.placed("1002", 1_000)), folder);
        assertThat(ExportFile.linesIn(folder)).containsExactly("order,total", "1001,25.00", "1002,10.00");
    }

    @Test
    @Order(2)
    void leavesOutCancelledOrders(@TempDir final Path folder) throws IOException {
        Exports.current().write(List.of(ShopOrder.placed("1001", 2_500), ShopOrder.cancelledOrder("1003", 4_000),
                ShopOrder.placed("1002", 1_000)), folder);
        assertThat(ExportFile.linesIn(folder)).containsExactly("order,total", "1001,25.00", "1002,10.00");
    }
}
