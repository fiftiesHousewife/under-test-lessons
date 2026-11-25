package io.github.fiftieshousewife.undertest.techniques.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/* AFTER: a test factory makes one test for each example file, named after the file. Every one runs and reports. */
class EachExampleTest {

    @TestFactory
    Stream<DynamicTest> eachExampleFormatsAsExpected() {
        return Formatters.examples().stream().map(example -> DynamicTest.dynamicTest(
                String.valueOf(example.getFileName()), () -> {
                    final List<String> lines = Formatters.lines(example);
                    assertThat(Formatters.current().forLetter(lines.get(0))).isEqualTo(lines.get(1));
                }));
    }
}
