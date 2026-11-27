package io.github.fiftieshousewife.undertest.techniques.fuzzing;

import com.code_intelligence.jazzer.junit.FuzzTest;
import java.nio.charset.StandardCharsets;

/*
 * AFTER: the contract as a fuzz test. Jazzer hands the reader inputs nobody wrote, guided by which lines of the code
 * each one reaches; the reader may read the line or refuse it, and any other exception fails the test, naming the
 * input. In an ordinary test run, Jazzer replays the inputs kept beside this test, each crash it ever found; the fuzz
 * task (./gradlew :lessons:fuzzing:fuzz) searches for new ones.
 */
class LabelReaderFuzzTest {

    private final LabelReader reader = LabelReaders.current();

    @FuzzTest(maxDuration = "30s")
    void readsOrRefusesEveryLine(final byte[] input) {
        try {
            reader.read(new String(input, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException refused) {
            // refusing a line it can't read is what the contract asks
        }
    }
}
