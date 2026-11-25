package io.github.fiftieshousewife.undertest.techniques.antipatterns.answer;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaCall;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import io.github.fiftieshousewife.undertest.techniques.antipatterns.AntiPatternRules;
import io.github.fiftieshousewife.undertest.techniques.antipatterns.Finding;
import io.github.fiftieshousewife.undertest.techniques.antipatterns.Register;
import io.github.fiftieshousewife.undertest.techniques.antipatterns.Registers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/*
 * THE EXERCISE'S ANSWER: the mocks-everything rule counts @Mock fields only, so a test class that makes its mocks with
 * calls to Mockito's mock() gets past it. This rule counts both, finds that class's tests, and the register, which
 * holds only what the lesson's rules found, refuses them as new. Run it with {@code -Pcode=answer}.
 */
class MockCallsAnswerTest {

    private static final String OLD_SHOP = "io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop";

    static long mocks(final JavaClass testClass) {
        return AntiPatternRules.mockFields(testClass) + testClass.getCodeUnits().stream()
                .flatMap(code -> code.getMethodCallsFromSelf().stream()).map(JavaCall::getTarget)
                .filter(call -> "mock".equals(call.getName()) && call.getOwner().isEquivalentTo(Mockito.class))
                .count();
    }

    @Test
    void findsTheTestsOfAClassThatMakesFiveMocksWithCalls() throws IOException {
        final List<JavaMethod> tests = AntiPatternRules.tests(new ClassFileImporter().importPackages(OLD_SHOP));
        final Predicate<JavaMethod> rule = test -> mocks(test.getOwner()) >= 5;
        final Register register = Registers.chosenByTheBuild();
        final List<Finding> unrecorded = register.unrecorded(
                AntiPatternRules.findings(tests, Map.of("mocks-everything", rule)));
        final List<String> lines = new ArrayList<>();
        lines.add("The answer's rule, counting mock() calls too: " + unrecorded.size()
                + " anti-patterns not in the register.");
        lines.addAll(register.report(unrecorded));
        Files.write(Path.of(System.getProperty("antiPatterns.answerReport")), lines);
        assertThat(unrecorded).extracting(Finding::test).as("tests with 5 or more mocks, not in the register")
                .containsExactly("CheckoutEmailTest.mailsTheReceipt", "CheckoutEmailTest.recordsTheSale");
    }
}
