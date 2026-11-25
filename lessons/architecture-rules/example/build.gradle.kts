plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Architecture rules: tests that check how the code is put together, not what it does.
//   ./gradlew :lessons:architecture-rules:test -Pcode=today   the web layer as it is today: the rule finds a shortcut
//   ./gradlew :lessons:architecture-rules:test                the web layer going through the service: every rule holds
dependencies {
    testImplementation(libs.archunit)
}

// `./gradlew :lessons:architecture-rules:test -Pcode=answer` checks today's web layer with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}
