package io.github.fiftieshousewife.undertest.techniques.fuzzing.answer;

import static org.assertj.core.api.Assertions.assertThat;

import com.code_intelligence.jazzer.junit.FuzzTest;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.Label;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.LabelReader;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.LabelReaders;
import java.nio.charset.StandardCharsets;

/*
 * THE EXERCISE'S ANSWER: a crash is only one way to be wrong. This fuzz test adds a rule of the courier's: a label the
 * reader accepts weighs 1 to 30,000 grams. Run it against today's reader with -Pcode=answer: it accepts a parcel
 * that weighs less than nothing.
 */
class WeightRuleAnswerTest {

    private final LabelReader reader = LabelReaders.current();

    @FuzzTest(maxDuration = "30s")
    void acceptsOnlyWeightsTheCourierTakes(final byte[] input) {
        final Label label;
        try {
            label = reader.read(new String(input, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException refused) {
            return;
        } catch (StringIndexOutOfBoundsException crash) {
            return; // the crash is the lesson's own fuzz test's to find; this test is about the weight
        }
        assertThat(label.grams()).as("grams on a label the reader accepted").isBetween(1, 30_000);
    }
}
