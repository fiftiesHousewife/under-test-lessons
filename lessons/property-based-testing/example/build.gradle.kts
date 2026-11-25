plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Property-based testing: state rules that must hold for every input, and let jetCheck try many.
//   ./gradlew :lessons:property-based-testing:test -Pcode=today   the splitter in use today: the property finds a bug
//   ./gradlew :lessons:property-based-testing:test                the fair splitter: every property holds
dependencies {
    testImplementation(libs.jetcheck)
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching splitters never shows a stale result
}

// `-Pcode=answer` runs the splitter in use today with the exercise's answer (answer/*AnswerTest), which -Pcode=today
// leaves out.
tasks.named<Test>("test") {
    if (providers.gradleProperty("code").orNull == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
}
