package io.github.fiftieshousewife.undertest.techniques.antipatterns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The checks over the old shop's tests. The first lists every anti-pattern the rules find, with its test, file and
 * line, for triage, and never fails on an old one. The second is the ratchet: it compares the findings with the
 * register, the committed file of anti-patterns already triaged, and refuses any it doesn't hold. With -Pcode=today
 * there is no register yet.
 */
class AntiPatternChecksTest {

    private static final List<JavaMethod> TESTS = AntiPatternRules.tests(new ClassFileImporter()
            .importPackages(AntiPatternChecksTest.class.getPackageName() + ".oldshop"));

    private final List<Finding> findings = AntiPatternRules.findings(TESTS, AntiPatternRules.RULES);
    private final Register register = Registers.chosenByTheBuild();

    @Test
    void listsEachAntiPatternWithItsTestFileAndLine() throws IOException {
        final long tests = findings.stream().map(Finding::test).distinct().count();
        final List<String> lines = new ArrayList<>();
        lines.add(String.format("The old shop's tests: %d anti-patterns in %d of %d tests, %d of them in the register.",
                findings.size(), tests, TESTS.size(), findings.size() - register.unrecorded(findings).size()));
        lines.addAll(register.report(findings));
        Files.write(Path.of(System.getProperty("antiPatterns.report")), lines);
        assertThat(findings).extracting(Finding::kind).as("kinds found").containsAll(AntiPatternRules.RULES.keySet());
    }

    @Test
    void refusesAnyAntiPatternTheRegisterDoesNotHold() {
        assertAll(
                () -> assertThat(register.unrecorded(findings).size()).as("anti-patterns not in the register").isZero(),
                () -> assertThat(register.fixed(findings)).as("register rows no longer found").isEmpty());
    }
}
