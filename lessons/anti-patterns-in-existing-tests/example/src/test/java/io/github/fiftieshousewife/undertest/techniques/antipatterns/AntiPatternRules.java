package io.github.fiftieshousewife.undertest.techniques.antipatterns;

import com.tngtech.archunit.core.domain.AccessTarget.MethodCallTarget;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.Mock;

/**
 * The anti-pattern checks: ArchUnit reads the old tests' compiled classes, and each rule says when a test shows one
 * kind of anti-pattern, as the toolkit's find-test-anti-patterns skill counts them. A disabled test counts as
 * disabled only.
 */
public final class AntiPatternRules {

    public static final Map<String, Predicate<JavaMethod>> RULES = Map.of(
            "disabled", AntiPatternRules::isDisabled,
            "mocks-everything", test -> mockFields(test.getOwner()) >= 5,
            "no-real-assertion", test -> calls(test).noneMatch(AntiPatternRules::isRealAssertion),
            "order-dependent", test -> test.getOwner().isAnnotatedWith(TestMethodOrder.class),
            "sleeps", test -> sleeps(test) || setUpMethods(test.getOwner()).anyMatch(AntiPatternRules::sleeps));

    /** Assertions that check only that something exists: they pass whatever the code does. */
    private static final Set<String> NOT_REAL = Set.of("assertNotNull", "assertNull", "assertThat", "isNotNull");

    private AntiPatternRules() {
    }

    static boolean isDisabled(final JavaMethod test) {
        return test.isAnnotatedWith(Disabled.class) || test.getOwner().isAnnotatedWith(Disabled.class);
    }

    /** A call to AssertJ, to JUnit's assertions or to Mockito's verify, other than one that checks only existence. */
    static boolean isRealAssertion(final MethodCallTarget call) {
        final String owner = call.getOwner().getName();
        final boolean assertion = owner.startsWith("org.assertj.") || "org.junit.jupiter.api.Assertions".equals(owner)
                || "org.mockito.Mockito".equals(owner) && "verify".equals(call.getName());
        return assertion && !NOT_REAL.contains(call.getName());
    }

    static boolean sleeps(final JavaCodeUnit code) {
        return calls(code).anyMatch(call -> "sleep".equals(call.getName())
                && (call.getOwner().isEquivalentTo(Thread.class) || call.getOwner().isEquivalentTo(TimeUnit.class)));
    }

    public static long mockFields(final JavaClass testClass) {
        return testClass.getFields().stream().filter(field -> field.isAnnotatedWith(Mock.class)).count();
    }

    private static Stream<MethodCallTarget> calls(final JavaCodeUnit code) {
        return code.getMethodCallsFromSelf().stream().map(JavaMethodCall::getTarget);
    }

    private static Stream<JavaMethod> setUpMethods(final JavaClass testClass) {
        return testClass.getMethods().stream().filter(method -> method.isAnnotatedWith(BeforeEach.class));
    }

    /** Every test in the classes: a method JUnit runs, such as one marked @Test. */
    public static List<JavaMethod> tests(final JavaClasses classes) {
        return classes.stream().flatMap(testClass -> testClass.getMethods().stream())
                .filter(method -> method.isMetaAnnotatedWith(Testable.class))
                .sorted(Comparator.comparing(JavaMethod::getFullName)).toList();
    }

    /** Each anti-pattern the rules find, sorted by kind, then by test, with the file and line where its code starts. */
    public static List<Finding> findings(final List<JavaMethod> tests, final Map<String, Predicate<JavaMethod>> rules) {
        return rules.entrySet().stream().sorted(Map.Entry.comparingByKey()).flatMap(rule -> tests.stream()
                .filter(test -> rule.getValue().test(test))
                .filter(test -> "disabled".equals(rule.getKey()) || !isDisabled(test))
                .map(test -> new Finding(rule.getKey(), test.getOwner().getSimpleName() + "." + test.getName(),
                        test.getSourceCodeLocation().getSourceFileName() + ":"
                                + test.getSourceCodeLocation().getLineNumber()))).toList();
    }
}
