package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.masking.testdata.PersonalDataDetector;
import io.github.fiftieshousewife.undertest.techniques.masking.testdata.TestData;
import org.junit.jupiter.api.Test;

/*
 * A guard on the test data, not a test of the shop's code: every field of the masked day is scanned for anything that
 * still looks personal, including fields nobody wrote a rule for, before the tests are trusted with it.
 */
class TestDataHasNothingPersonalTest {

    @Test
    void theMaskedDayHasNothingThatLooksPersonal() {
        final PersonalDataDetector guard = new PersonalDataDetector(TestData.customerNames("capture"));

        assertThat(guard.findings(TestData.masked("capture"))).as("personal data left in the test data")
                .isEmpty();
    }
}
