package io.github.fiftieshousewife.undertest.techniques.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;

/*
 * AFTER: an architecture rule. It reads the compiled classes and checks which layer may use which: web may use the
 * service, the service may use storage, and nothing else may reach storage. It checks the web classes chosen with
 * -Pcode (today or fixed).
 */
class LayerRulesTest {

    private static final String BASE = LayerRulesTest.class.getPackageName();

    private final JavaClasses classes = new ClassFileImporter().importPackages(BASE + ".storage", BASE + ".service",
            BASE + ".web." + System.getProperty("code", "fixed"));

    @Test
    void onlyTheServiceLayerUsesStorage() {
        final ArchRule layers = layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("Web").definedBy(BASE + ".web..")
                .layer("Service").definedBy(BASE + ".service..")
                .layer("Storage").definedBy(BASE + ".storage..")
                .whereLayer("Web").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Web")
                .whereLayer("Storage").mayOnlyBeAccessedByLayers("Service");
        check(layers.evaluate(classes));
    }

    /* Fails with ArchUnit's own report, with the long package names taken out so it reads easily. */
    private static void check(final EvaluationResult result) {
        if (result.hasViolation()) {
            throw new AssertionError(String.join(System.lineSeparator(), result.getFailureReport().getDetails())
                    .replace(BASE + ".", "").replaceAll(" in \\(\\w+\\.java:\\d+\\)", "").strip());
        }
    }
}
