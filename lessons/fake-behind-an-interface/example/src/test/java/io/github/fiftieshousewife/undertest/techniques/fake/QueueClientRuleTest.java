package io.github.fiftieshousewife.undertest.techniques.fake;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

/*
 * Keeps the fake honest: only the connection code may use the queue's client library. Everything else goes through
 * the shop's own interface, so a test can always hand it the fake. It reads the compiled classes, today's and fixed.
 */
class QueueClientRuleTest {

    private static final String BASE = QueueClientRuleTest.class.getPackageName();

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages(BASE);

    @Test
    void onlyTheConnectionCodeUsesTheQueueClient() {
        final ArchRule rule = noClasses()
                .that().resideOutsideOfPackages(BASE + ".connection..", BASE + ".client..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".client..")
                .because("the rest of the shop reaches the queue through the MessageQueue interface");
        rule.check(classes);
    }
}
