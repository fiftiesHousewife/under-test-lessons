package io.github.fiftieshousewife.undertest.techniques.architecture.answer;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: one more rule, written as a list of what the web layer may use (itself, the service layer and
 * Java's own classes) rather than what it may not. Anything else, storage or a library nobody agreed to, breaks it.
 * Run it against today's admin page with {@code -Pcode=answer}.
 */
class WebDependenciesAnswerTest {

    private static final String BASE = "io.github.fiftieshousewife.undertest.techniques.architecture";

    private final JavaClasses web = new ClassFileImporter()
            .importPackages(BASE + ".web." + System.getProperty("code", "fixed"));

    @Test
    void theWebLayerUsesOnlyTheServiceLayerAndJava() {
        final ArchRule rule = classes().that().resideInAPackage(BASE + ".web..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(BASE + ".web..", BASE + ".service..", "java..");
        final List<String> breaks = rule.evaluate(web).getFailureReport().getDetails().stream()
                .map(line -> line.replace(BASE + ".", "").replaceAll(" in \\(\\w+\\.java:\\d+\\)", ""))
                .toList();
        assertThat(breaks).as("web classes that use something other than the service layer or Java").isEmpty();
    }
}
