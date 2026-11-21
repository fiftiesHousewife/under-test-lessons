plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Architecture rules: tests that check how the code is put together, not what it does.
//   ./gradlew :lessons:architecture-rules:test -Pcode=today   the web layer as it is today: the rule finds a shortcut
//   ./gradlew :lessons:architecture-rules:test                the web layer going through the service: every rule holds
dependencies {
    testImplementation(libs.archunit)
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}
