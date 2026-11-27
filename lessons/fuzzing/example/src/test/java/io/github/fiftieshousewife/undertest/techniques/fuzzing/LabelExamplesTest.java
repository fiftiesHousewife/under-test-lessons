package io.github.fiftieshousewife.undertest.techniques.fuzzing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/*
 * BEFORE: the examples someone wrote, years ago: a good label, and the two mistakes they thought of. Every one is a
 * line a person typed, so every postcode has its space.
 */
class LabelExamplesTest {

    private final LabelReader reader = LabelReaders.current();

    @Test
    void readsAGoodLabel() {
        assertThat(reader.read("Ada Brennan;SW1A 1AA;500")).isEqualTo(new Label("Ada Brennan", "SW1A", 500));
    }

    @Test
    void refusesALabelWithAFieldMissing() {
        assertThatThrownBy(() -> reader.read("Ada Brennan;SW1A 1AA")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refusesAWeightThatIsNotANumber() {
        assertThatThrownBy(() -> reader.read("Ada Brennan;SW1A 1AA;heavy"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
