package io.github.fiftieshousewife.undertest.techniques.tempfolders.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.tempfolders.ExportFile;
import io.github.fiftieshousewife.undertest.techniques.tempfolders.Exports;
import io.github.fiftieshousewife.undertest.techniques.tempfolders.ShopOrder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The exercise's answer: on a day when every order was cancelled, the accounts system still gets a file. */
class AllCancelledAnswerTest {

    @Test
    void aDayOfCancelledOrdersWritesJustTheHeader(@TempDir final Path folder) throws IOException {
        Exports.current().write(List.of(ShopOrder.cancelledOrder("1004", 1_500)), folder);

        assertThat(ExportFile.linesIn(folder)).containsExactly("order,total");
    }
}
