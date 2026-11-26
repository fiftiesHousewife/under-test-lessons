plugins {
    alias(libs.plugins.java.conventions)
}

// Message consumers: a JMS consumer tested on a real broker, Apache Artemis, started inside the test with no network,
// no files and no container.
//   ./gradlew :lessons:message-consumers:test -Pcode=today   today's consumer: payments lost, nothing in the dead-letter queue
//   ./gradlew :lessons:message-consumers:test                the fixed consumer: redelivered, and the bad message kept
dependencies {
    compileOnly(libs.jakarta.jms.api) // the application server or the client library provides it in production
    testImplementation(libs.artemis.jakarta.server) // the broker, and its JMS client, run inside the test
    testImplementation(libs.mockito.core) // only for the "before" test's hand-built message
}

// `./gradlew :lessons:message-consumers:test -Pcode=answer` runs today's consumer with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
    // The broker's network library and its optional native journal ask the JDK for low-level access; allowing it
    // here keeps the JDK's warnings out of the test output.
    jvmArgs("--enable-native-access=ALL-UNNAMED", "--sun-misc-unsafe-memory-access=allow")
}

// Today's consumer is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
