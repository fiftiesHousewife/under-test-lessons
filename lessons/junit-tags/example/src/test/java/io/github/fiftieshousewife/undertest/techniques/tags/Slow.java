package io.github.fiftieshousewife.undertest.techniques.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/** Marks a test, or a whole test class, as slow: it carries JUnit's tag "slow", which the build filters on. */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("slow")
public @interface Slow {
}
