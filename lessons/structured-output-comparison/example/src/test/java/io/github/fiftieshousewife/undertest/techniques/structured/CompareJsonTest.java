package io.github.fiftieshousewife.undertest.techniques.structured;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;

/*
 * AFTER: JSONUnit compares the JSON as a structure: every field and every value, in any field order. The two fields
 * that change on every run only have to be there, as text.
 */
class CompareJsonTest {

    @Test
    void summaryHasTheExpectedLinesAndTotal() {
        assertThatJson(Summaries.jsonForTheOrder())
                .isEqualTo("""
                        {
                          "lines": [
                            {"item": "Tea", "pricePence": 250, "quantity": 4},
                            {"item": "Biscuits", "pricePence": 120, "quantity": 1}
                          ],
                          "totalPence": 1120,
                          "id": "${json-unit.any-string}",
                          "generatedAt": "${json-unit.any-string}"
                        }
                        """);
    }
}
