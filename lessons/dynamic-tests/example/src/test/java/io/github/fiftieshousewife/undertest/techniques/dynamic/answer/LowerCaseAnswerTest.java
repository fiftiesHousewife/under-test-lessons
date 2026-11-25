package io.github.fiftieshousewife.undertest.techniques.dynamic.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.dynamic.Formatters;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/*
 * The exercise's answer: one more example, a record in lower case as a web form sends it. Its file is in the test
 * resources' answer folder, so -Pcode=today leaves it out; adding it to the examples folder works the same way.
 */
class LowerCaseAnswerTest {

    @TestFactory
    Stream<DynamicTest> lowerCaseRecordsFormatAsExpected() {
        return DynamicTest.stream(Formatters.examples("/answer").stream(),
                example -> String.valueOf(example.getFileName()), LowerCaseAnswerTest::formatsAsExpected);
    }

    private static void formatsAsExpected(final Path example) {
        final List<String> lines = Formatters.lines(example);
        assertThat(Formatters.current().forLetter(lines.get(0))).isEqualTo(lines.get(1));
    }
}
