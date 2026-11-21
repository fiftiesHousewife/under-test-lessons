import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Property-based testing: state rules that must hold for every input, and let jetCheck try many.
//   ./gradlew :lessons:property-based-testing:test -Psplitter=current   the splitter in use today: the property finds a bug
//   ./gradlew :lessons:property-based-testing:test                      the fair splitter: every property holds
dependencies {
    testImplementation(libs.jetcheck)
}

tasks.test {
    systemProperty("splitter", providers.gradleProperty("splitter").getOrElse("fair"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching splitters never shows a stale result
    testLogging {
        events("passed", "failed")
        exceptionFormat = TestExceptionFormat.FULL // show the smallest failing bill...
        showStackTraces = false // ...without a stack trace
        showCauses = false
    }
}
