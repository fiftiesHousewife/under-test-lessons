package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/* BEFORE: every test writes to the same fixed folder, and nothing ever cleans it up. */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FixedFolderTest {

    private static final Path OUT = Path.of("build", "out");

    @Test
    @Order(1)
    void writesOneLinePerOrder() throws IOException {
        Files.createDirectories(OUT);
        Exports.current().write(List.of(ShopOrder.placed("1001", 2_500), ShopOrder.placed("1002", 1_000)), OUT);
        assertThat(ExportFile.linesIn(OUT)).containsExactly("order,total", "1001,25.00", "1002,10.00");
    }

    @Test
    @Order(2)
    void leavesOutCancelledOrders() throws IOException {
        Files.createDirectories(OUT);
        Exports.current().write(List.of(ShopOrder.placed("1001", 2_500), ShopOrder.cancelledOrder("1003", 4_000),
                ShopOrder.placed("1002", 1_000)), OUT);
        assertThat(ExportFile.linesIn(OUT)).containsExactly("order,total", "1001,25.00", "1002,10.00");
    }
}
