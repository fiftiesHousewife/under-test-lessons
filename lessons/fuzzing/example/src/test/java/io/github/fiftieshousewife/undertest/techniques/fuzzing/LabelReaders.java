package io.github.fiftieshousewife.undertest.techniques.fuzzing;

import io.github.fiftieshousewife.undertest.techniques.fuzzing.fixed.FixedLabelReader;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.today.TodaysLabelReader;
import java.util.Set;

/**
 * The reader the tests run against: the fixed one, or today's with -Pcode=today, and with -Pcode=answer, which runs
 * the exercise's answer against today's reader.
 */
public final class LabelReaders {

    private LabelReaders() {
    }

    public static LabelReader current() {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        return today ? new TodaysLabelReader() : new FixedLabelReader();
    }
}
