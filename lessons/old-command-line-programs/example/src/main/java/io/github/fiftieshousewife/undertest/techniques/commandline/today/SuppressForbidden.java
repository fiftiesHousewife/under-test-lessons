package io.github.fiftieshousewife.undertest.techniques.commandline.today;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lets today's program make calls the build's forbidden-API check refuses, such as System.out. The check accepts any
 * annotation with this name; it is allowed in this package only (see the lessons' decision records).
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@interface SuppressForbidden {
}
